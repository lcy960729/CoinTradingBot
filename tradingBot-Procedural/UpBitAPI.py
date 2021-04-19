import requests
import MyToken
from urllib.parse import urlencode

url = "https://api.upbit.com/v1"


def getDayCandle(market, count):
    requestUrl = url + "/candles/days"

    querystring = {"market": market,
                   "count": count}

    response = requests.get(requestUrl, params=querystring)

    return response.json()


def getMinuteCandle(market, minute, count):
    requestUrl = url + "/candles/minutes/" + str(minute)

    querystring = {"market": market,
                   "count": count}

    response = requests.get(requestUrl, params=querystring)

    return response.json()


def getTicker(market):
    requestUrl = url + "/ticker" 

    querystring = {"markets": market}

    response = requests.get(requestUrl, params=querystring)

    return response.json()

def getMyAccount():
    requestUrl = url + "/accounts"
    headers = {"Authorization": MyToken.get()}
    response = requests.get(requestUrl, headers=headers)
    return response.json()

def getAllMarket():
    requestUrl = url + "/market/all"
    response = requests.get(requestUrl)
    return response.json()


def getWallet():
    requestUrl = url + "/status/wallet"
    headers = {"Authorization": MyToken.get()}

    response = requests.get(requestUrl, headers=headers)
    return response.json()


def order(market, bid, ord_type, volume, price = ''):
    requestUrl = url + "/orders"
    query_params = {'market': market,
                    'side': bid,
                    'volume': volume,
                    'price': price,
                    'ord_type': ord_type
                    }

    headers = {"Authorization": MyToken.get(query_params)}

    response = requests.post(requestUrl, params=query_params, headers=headers)
    return response.json()


def orderInfo(uuid):
    requestUrl = url + "/order"
    query_params = {'uuid': uuid}

    headers = {"Authorization": MyToken.get(query_params)}

    response = requests.get(requestUrl, params=query_params, headers=headers)
    return response.json()

def deleteOrder(uuid):
    requestUrl = url + "/order"
    query_params = {'uuid': uuid}

    headers = {"Authorization": MyToken.get(query_params)}

    response = requests.delete(requestUrl, params=query_params, headers=headers)
    return response.json()

def getOrderBook(market):
    requestUrl = url + "/orderbook" 

    querystring = {"markets": market}

    response = requests.get(requestUrl, params=querystring)

    return response.json()
