package com.essence.business.xqh.api.waterandrain.dto;

import java.util.List;

/**
 * @author fengpp
 * 2021/2/4 20:01
 */
public class MonthRainfallDto {
    private List<RainfallDto> lessOne;//小于1
    private List<RainfallDto> betweenOneAndTen;//1-10
    private List<RainfallDto> betweenTenAndTwentyFive;//10-25
    private List<RainfallDto> betweenTwentyFiveAndFifty;//25-50
    private List<RainfallDto> betweenFiftyAndOneHundred;//50-100
    private List<RainfallDto> betweenOneHundredAndTwoHundred;//100-200
    private List<RainfallDto> betweenTwoAndFourHundred;//200-400
    private List<RainfallDto> betweenFourAndEightHundred;//400-800
    private List<RainfallDto> beyondEightHundred;//大于800

    public List<RainfallDto> getLessOne() {
        return lessOne;
    }

    public void setLessOne(List<RainfallDto> lessOne) {
        this.lessOne = lessOne;
    }

    public List<RainfallDto> getBetweenOneAndTen() {
        return betweenOneAndTen;
    }

    public void setBetweenOneAndTen(List<RainfallDto> betweenOneAndTen) {
        this.betweenOneAndTen = betweenOneAndTen;
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

    public List<RainfallDto> getBetweenOneHundredAndTwoHundred() {
        return betweenOneHundredAndTwoHundred;
    }

    public void setBetweenOneHundredAndTwoHundred(List<RainfallDto> betweenOneHundredAndTwoHundred) {
        this.betweenOneHundredAndTwoHundred = betweenOneHundredAndTwoHundred;
    }

    public List<RainfallDto> getBetweenTwoAndFourHundred() {
        return betweenTwoAndFourHundred;
    }

    public void setBetweenTwoAndFourHundred(List<RainfallDto> betweenTwoAndFourHundred) {
        this.betweenTwoAndFourHundred = betweenTwoAndFourHundred;
    }

    public List<RainfallDto> getBetweenFourAndEightHundred() {
        return betweenFourAndEightHundred;
    }

    public void setBetweenFourAndEightHundred(List<RainfallDto> betweenFourAndEightHundred) {
        this.betweenFourAndEightHundred = betweenFourAndEightHundred;
    }

    public List<RainfallDto> getBeyondEightHundred() {
        return beyondEightHundred;
    }

    public void setBeyondEightHundred(List<RainfallDto> beyondEightHundred) {
        this.beyondEightHundred = beyondEightHundred;
    }
}
