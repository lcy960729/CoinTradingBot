package com.cy.tradingbot.services.coin;

import com.cy.tradingbot.domain.Candle;
import com.cy.tradingbot.domain.Coin;
import com.cy.tradingbot.domain.entity.User;
import com.cy.tradingbot.services.candle.GetCandlesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UpdateTargetPriceAndMovingAverageForCoinService {
    @Autowired
    private GetCandlesService getCandlesService;

    public void update(User user, Coin coin) {
        List<Candle> candles = getCandlesService.getCandles(coin.getCoinName(), user.getMaxOfCandles());

        coin.setCandles(candles);

        coin.setTargetPrice(calcTargetPrice(candles));
        coin.setMovingAverageList(calcMovingAverage(candles, user.getNumOfMovingAverageWindow()));
    }

    private List<Double> calcMovingAverage(List<Candle> candles, int numOfMovingAverageWindow) {
        double sum = 0;
        List<Double> ret = new ArrayList<>();

        for (int i = 1; i < numOfMovingAverageWindow; ++i) {
            sum += candles.get(candles.size() - i).getTradePrice();
        }

        for (int i = numOfMovingAverageWindow; i <= candles.size(); ++i) {
            sum += candles.get(candles.size() - i).getTradePrice();
            ret.add(sum / numOfMovingAverageWindow);
            sum -= candles.get(candles.size() - i + numOfMovingAverageWindow - 1).getTradePrice();
        }

        return ret;
    }

    private double calcTargetPrice(List<Candle> candles) {
        if (candles.size() < 2) return -1;

        Candle todayCandle = candles.get(0);
        Candle yesterdayCandle = candles.get(1);

        double midPrice = yesterdayCandle.getHighPrice() - yesterdayCandle.getLowPrice();

        double k = candles.stream()
                .mapToDouble(Candle::calcKValue)
                .average()
                .orElse(0);

        return todayCandle.getOpeningPrice() + (midPrice * k);
    }
}
