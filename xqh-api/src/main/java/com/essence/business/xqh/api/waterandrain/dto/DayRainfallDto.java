package com.essence.business.xqh.api.waterandrain.dto;

import java.util.List;

/**
 * @author fengpp
 * 2021/2/4 19:11
 */
public class DayRainfallDto {
    private List<RainfallDto> zero;//无降雨
    private List<RainfallDto> betweenZeroAndTen;//0-10
    private List<RainfallDto> betweenTenAndTwentyFive;//10-25
    private List<RainfallDto> betweenTwentyFiveAndFifty;//25-50
    private List<RainfallDto> betweenFiftyAndOneHundred;//50-100
    private List<RainfallDto> betweenOneHundredAndTwoHundredAndFifty;//100-250
    private List<RainfallDto> beyondTwoHundredAndFifty;//大于250

    public List<RainfallDto> getZero() {
        return zero;
    }

    public void setZero(List<RainfallDto> zero) {
        this.zero = zero;
    }

    public List<RainfallDto> getBetweenZeroAndTen() {
        return betweenZeroAndTen;
    }

    public void setBetweenZeroAndTen(List<RainfallDto> betweenZeroAndTen) {
        this.betweenZeroAndTen = betweenZeroAndTen;
    }

    public List<RainfallDto> getBetweenTenAndTwentyFive() {
        return betweenTenAndTwentyFive;
    }

    public void setBetweenTenAndTwentyFive(List<RainfallDto> betweenTenAndTwentyFive) {
        this.betweenTenAndTwentyFive = betweenTenAndTwentyFive;
    }

    public List<RainfallDto> getBetweenTwentyFiveAndFifty() {
        return betweenTwentyFiveAndFifty;
    }

    public void setBetweenTwentyFiveAndFifty(List<RainfallDto> betweenTwentyFiveAndFifty) {
        this.betweenTwentyFiveAndFifty = betweenTwentyFiveAndFifty;
    }

    public List<RainfallDto> getBetweenFiftyAndOneHundred() {
        return betweenFiftyAndOneHundred;
    }

    public void setBetweenFiftyAndOneHundred(List<RainfallDto> betweenFiftyAndOneHundred) {
        this.betweenFiftyAndOneHundred = betweenFiftyAndOneHundred;
    }

    public List<RainfallDto> getBetweenOneHundredAndTwoHundredAndFifty() {
        return betweenOneHundredAndTwoHundredAndFifty;
    }

    public void setBetweenOneHundredAndTwoHundredAndFifty(List<RainfallDto> betweenOneHundredAndTwoHundredAndFifty) {
        this.betweenOneHundredAndTwoHundredAndFifty = betweenOneHundredAndTwoHundredAndFifty;
    }

    public List<RainfallDto> getBeyondTwoHundredAndFifty() {
        return beyondTwoHundredAndFifty;
    }

    public void setBeyondTwoHundredAndFifty(List<RainfallDto> beyondTwoHundredAndFifty) {
        this.beyondTwoHundredAndFifty = beyondTwoHundredAndFifty;
    }
}
