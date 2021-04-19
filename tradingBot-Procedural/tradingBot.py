import UpBitAPI
from datetime import datetime, timedelta
import time
import pandas as pd
import json
from pytz import timezone


maxOfCandles = 0
maWindow = 0
maxOfPurchaseMarket = 0


def calc_K_value(candles):
    average = 0
    for candle in candles:
        if ((candle['high_price'] - candle['low_price']) > 0):
            average += 1 - abs(candle['opening_price'] - candle['trade_price']
                               ) / (candle['high_price'] - candle['low_price'])

    average /= maxOfCandles

    return average


def calc_target_price(candles, k):
    high_price = candles[1]["high_price"]
    low_price = candles[1]["low_price"]

    mid_price = high_price - low_price

    opening_price = candles[0]['opening_price']

    target_price = opening_price + (mid_price * k)

    return target_price


def get_target_price(mode, market, minute):
    candles = ''

    if (mode == 'day'):
        candles = UpBitAPI.getDayCandle(market, maxOfCandles+1)
    else:
        candles = UpBitAPI.getMinuteCandle(market, minute, maxOfCandles+1)

    k = calc_K_value(candles[1:])

    return calc_target_price(candles, k)


def get_current_price(market):
    return UpBitAPI.getTicker(market)[0]['trade_price']


def get_volume(market):
    myAccount = UpBitAPI.getMyAccount()
    for account in myAccount:
        if (account['currency'] == market):
            return float(account['balance'])

    return 0


def get_sell_price(market):
    orderBook = UpBitAPI.getOrderBook(market)
    orderbook_units = orderBook[0]['orderbook_units']
    return float(orderbook_units[0]['ask_price'])


def get_yesterDay_volatility(market, current_price):
    candles = UpBitAPI.getDayCandle(market, 2)[1:]
    high_price = candles[0]["high_price"]
    low_price = candles[0]["low_price"]

    return (high_price-low_price) / current_price


def buy(market, maList, numOfPurchaseMarket):
    fee = 0.9994
    sell_price = get_sell_price(market)
    movingAverageScore = get_Score_MovingAverage(maList, sell_price)
    volatility = 1-(0.02 / get_yesterDay_volatility(market, sell_price) / maxOfPurchaseMarket)

    KRWBalance = (get_volume('KRW') / (maxOfPurchaseMarket-numOfPurchaseMarket)) * movingAverageScore * volatility * fee

    if (KRWBalance < 5000):
        return True
    print("-------------------------------------------------------------------------------")
    print('[' + datetime.now(timezone('Asia/Seoul')).strftime("%y-%m-%d %H:%M:%S") +
          '] ' + '[' + market + ']\t 매수를 시도합니다...\n' + '현재가 : ' + str(current_price) + '  \t목표가 : ' + str(target_price[market]))

    volume = KRWBalance / sell_price

    response = UpBitAPI.order(market, 'bid', 'limit', volume, sell_price)

    if (not('uuid' in response)):
        print('[' + datetime.now(timezone('Asia/Seoul')).strftime("%y-%m-%d %H:%M:%S") + '] ' + '[' + market + ']\t 매수 실패... \t' +
              str(response['error']['message']))
        return False

    time.sleep(2)

    uuid = response['uuid']
    orderInfo = UpBitAPI.orderInfo(uuid)

    if (orderInfo['state'] == 'wait'):
        UpBitAPI.deleteOrder(uuid)
        print('[' + datetime.now(timezone('Asia/Seoul')).strftime("%y-%m-%d %H:%M:%S") +
              '] ' + '[' + market + ']\t 매수 취소... \t매수 주문이 체결되지 않았습니다')
        return False

    print('[' + datetime.now(timezone('Asia/Seoul')).strftime("%y-%m-%d %H:%M:%S") + '] ' + '[' + market + ']\t 매수 성공... \t\t매수가 : ' +
          str(sell_price) + '\t\t매수량 : ' + str(volume))

    return True


def sell(market):
    volume = get_volume(market.split('-')[1])

    if (volume <= 0):
        return True

    print('[' + datetime.now(timezone('Asia/Seoul')).strftime("%y-%m-%d %H:%M:%S") +
          '] ' + '[' + market + ']\t' + " 매도를 시도합니다...")

    response = UpBitAPI.order(market, 'ask', 'market', volume)

    if (not('uuid' in response)):
        print('[' + datetime.now(timezone('Asia/Seoul')).strftime("%y-%m-%d %H:%M:%S") + '] ' + '[' + market + ']\t 매도 실패... \t\t' +
              str(response['error']['message']))
        return False

    time.sleep(2)

    uuid = response['uuid']
    orderInfo = UpBitAPI.orderInfo(uuid)

    if (orderInfo['state'] == 'wait'):
        UpBitAPI.deleteOrder(uuid)
        print('[' + datetime.now(timezone('Asia/Seoul')).strftime("%y-%m-%d %H:%M:%S") +
              '] ' + '[' + market + ']\t 매도 취소... \t\t매도 주문이 체결되지 않았습니다')
        return False

    print('[' + datetime.now(timezone('Asia/Seoul')).strftime("%y-%m-%d %H:%M:%S") + '] ' + '[' + market + ']\t 매도 성공... \t\t매도가 : ' +
          str(orderInfo['trades'][0]['price']) + '\t\t매도량 : ' + str(volume))

    return True


def isClosed(closingTime):
    nowTime = datetime.now(timezone('Asia/Seoul'))
    return closingTime + timedelta(seconds=10) > nowTime > closingTime


def getClosingTime(mode, time):
    if (mode == 'day'):
        return datetime.now(timezone('Asia/Seoul')).replace(hour=9, minute=0, second=0) + timedelta(days = 1)
    else:
        if (time >= 60):
            timeTable = []

            i = time
            for i in range(int(24*60/time)):
                timeTable.append(int((i+1)*time/60))

            hour = upper_bound(datetime.now(
                timezone('Asia/Seoul')).hour, timeTable) % 24
            result = datetime.now(timezone('Asia/Seoul')
                                  ).replace(hour=hour, minute=0, second=0)

            if (hour == 0):
                result += timedelta(days=1)

            return result
        else:
            timeTable = []

            i = time
            for i in range(int(60/time)):
                timeTable.append(int((i+1)*time))

            minute = upper_bound(datetime.now(
                timezone('Asia/Seoul')).minute, timeTable) % 60
            result = datetime.now(timezone('Asia/Seoul')
                                  ).replace(minute=minute, second=0)

            if (minute == 0):
                result += timedelta(hours=1)

            return result


def upper_bound(n, list):
    l = -1
    r = len(list)

    while(l+1 < r):
        m = int((l+r) / 2)

        if (list[m] > n):
            r = m
        else:
            l = m

    return list[r]


def calc_MovingAverage(candles, day):
    close = pd.DataFrame(columns=['day', 'close'])

    i = 1
    for candle in candles[::-1]:
        df = pd.DataFrame(index=[i], columns=['day', 'close'], data=[
                          [candle['candle_date_time_kst'], candle['trade_price']]])
        close = close.append(df)
        i += 1

    ma = close.rolling(window=maWindow).mean()
    return ma['close'].tolist()


def get_MovingAverage(mode, market, minute):
    if (mode == 'day'):
        candles = UpBitAPI.getDayCandle(market, maxOfCandles+1)[1:]
    else:
        candles = UpBitAPI.getMinuteCandle(market, minute, maxOfCandles+1)[1:]

    return calc_MovingAverage(candles, maxOfCandles)


def get_Score_MovingAverage(maList, current_price):
    score = 0
    for ma in maList:
        if (current_price > ma):
            score += 1

    return score/(maxOfCandles - maWindow+1)


def get_purchaseList(markets):
    purchaseList = {}

    for market in markets:
        purchaseList[market] = 0

    myAccount = UpBitAPI.getMyAccount()
    for account in myAccount:
        if (account['currency'] == 'KRW'):
            continue

        coinName = 'KRW-' + account['currency']

        if (float(account['balance']) > 0):
            purchaseList[coinName] = float(account['avg_buy_price'])

    return purchaseList


def lossRateOver5per(current_price, buy_price):
    lossRate = 0
    if (buy_price != 0):
        lossRate = (current_price - buy_price) / buy_price * 100

    return lossRate <= float(-5)

def marginRateOver5per(current_price, buy_price):
    marginRate = 0
    if (buy_price != 0):
        marginRate = (current_price - buy_price) / buy_price * 100

    return marginRate >= float(5)

def get_AllMarkets():
    markets = UpBitAPI.getAllMarket()

    result = []
    for market in markets:
        if (market['market'].find('KRW') != -1):
            result.append(market['market'])

    return result


if __name__ == '__main__':
    markets = []
    mode = ''
    minute = ''
    whiteList = {}
    blackList = set()
    target_price = {}
    movingAverageList = {}

    with open('setting.json') as json_file:
        json_data = json.load(json_file)

        mode = json_data['period']
        minute = json_data['minute']

        if (json_data['Allmarket']):
            markets = get_AllMarkets()
        else :
            markets = json_data['markets']
            for i, market in enumerate(markets):
                markets[i] = 'KRW-' + market

        whiteList = json_data['whiteList']
        maxOfCandles = json_data['maxOfCandles']
        maWindow = json_data['maWindow']
        maxOfPurchaseMarket = json_data['maxOfPurchaseMarket']

    # 건들지 마세요~~~~~~~~~~~~~~~~
    print('[' + datetime.now(timezone('Asia/Seoul')
                             ).strftime("%y-%m-%d %H:%M:%S") + '] ' + "초기화 시작!!!")
    for market in markets:
        target_price[market] = get_target_price(mode, market, minute)
        movingAverageList[market] = get_MovingAverage(mode, market, minute)
        time.sleep(0.1)

    closingTime = getClosingTime(mode, minute)
    purchaseList = get_purchaseList(markets)
    print('[' + datetime.now(timezone('Asia/Seoul')
                             ).strftime("%y-%m-%d %H:%M:%S") + '] ' + "초기화 끝!!!")

    # 시작~~~~~~~~~~~~~~~
    cur = -1
    while(True):
        try:
            if (datetime.now(timezone('Asia/Seoul')).minute % 5 == 0):
                with open("log.txt", 'w') as f:
                    f.write('[' + datetime.now(timezone('Asia/Seoul')).strftime("%y-%m-%d %H:%M:%S") + '] 프로그램 동작 중...')

            ## 장 마감
            if (isClosed(closingTime)):
                time.sleep(30)

                print(
                    "###############################################################################")
                print('[' + datetime.now(timezone('Asia/Seoul')
                                         ).strftime("%y-%m-%d %H:%M:%S") + '] ' + "장 마감!!! 다음 장을 위한 재설정 시작")
                print('[' + datetime.now(timezone('Asia/Seoul')
                                         ).strftime("%y-%m-%d %H:%M:%S") + '] ' + "마감시간을 재설정...")
                closingTime = getClosingTime(mode, minute)

                for market in markets:
                    if (purchaseList[market] == 0 or market in whiteList):
                        continue

                    while(True):
                        if (sell(market)):
                            break

                    print('[' + datetime.now(timezone('Asia/Seoul')).strftime(
                        "%y-%m-%d %H:%M:%S") + ']' + '[' + market + ']\t' + " 목표가를 새로 받아오는 중...")
                    target_price[market] = get_target_price(
                        mode, market, minute)

                    print('[' + datetime.now(timezone('Asia/Seoul')).strftime(
                        "%y-%m-%d %H:%M:%S") + ']' + '[' + market + ']\t' + " 이동 평균값을 계산하는 중...\n")
                    movingAverageList[market] = get_MovingAverage(
                        mode, market, minute)

                    blackList.clear()
                    time.sleep(0.5)

                cur = -1
                print('[' + datetime.now(timezone('Asia/Seoul')).strftime(
                    "%y-%m-%d %H:%M:%S") + ']' + " 장 마감 재설정 완료!!! 10초 뒤 다음 장 시작")
                time.sleep(10)

            # 장 시작
            cur = (cur + 1) % len(markets)
            market = markets[cur]

            if (market in whiteList): continue

            current_price = get_current_price(market)
            purchaseList = get_purchaseList(markets)

            numOfPurchaseMarket = 0
            for i in purchaseList:
                if (purchaseList[i] > 0):
                    numOfPurchaseMarket += 1

            time.sleep(0.3)

            print('[' + datetime.now(timezone('Asia/Seoul')).strftime("%y-%m-%d %H:%M:%S") + '] 현재상황 ' +
                '\t구입한 코인 수 : ' + str(numOfPurchaseMarket) + '/' + str(maxOfPurchaseMarket), end='\r')

            if (lossRateOver5per(current_price, purchaseList[market]) or marginRateOver5per(current_price, purchaseList[market])):
                print(
                    "###############################################################################")
                if (lossRateOver5per(current_price, purchaseList[market])):
                    print('[' + datetime.now(timezone('Asia/Seoul')
                                         ).strftime("%y-%m-%d %H:%M:%S") + '] ' + "\n손실로 인한 매도 시작")
                else:
                    print('[' + datetime.now(timezone('Asia/Seoul')
                                         ).strftime("%y-%m-%d %H:%M:%S") + '] ' + "\n이익으로 인한 매도 시작")

                print('[' + datetime.now(timezone('Asia/Seoul')).strftime("%y-%m-%d %H:%M:%S") + '] ' +
                      '[' + market + ']\t' + " 매도를 시도합니다...")
                
                while(True):
                    if (sell(market)):
                        break

                print('[' + datetime.now(timezone('Asia/Seoul')).strftime("%y-%m-%d %H:%M:%S") + '] ' +
                      '[' + market + ']\t' + " 목표가를 새로 받아오는 중...")
                target_price[market] = get_target_price(
                    mode, market, minute)

                print('[' + datetime.now(timezone('Asia/Seoul')).strftime("%y-%m-%d %H:%M:%S") + '] ' +
                      '[' + market + ']\t' + " 이동 평균값을 계산하는 중...")
                movingAverageList[market] = get_MovingAverage(
                    mode, market, minute)
                
                blackList.add(market)
                time.sleep(1)

            elif (numOfPurchaseMarket < maxOfPurchaseMarket and purchaseList[market] == 0):
                if (current_price > target_price[market] and not (market in blackList)):
                    buy(market, movingAverageList[market], numOfPurchaseMarket)

        except Exception as e:
            print('Error 발생', e)