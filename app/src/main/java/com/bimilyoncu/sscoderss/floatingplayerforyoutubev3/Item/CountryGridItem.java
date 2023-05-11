package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item;

/**
 * Created by Sabahattin on 20/09/2019.
 */

public class CountryGridItem {
    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCounrtyFlagUrl() {
        return counrtyFlagUrl;
    }

    public void setCounrtyFlagUrl(String counrtyFlagUrl) {
        this.counrtyFlagUrl = counrtyFlagUrl;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    private String countryName;
    private String countryCode;
    private String counrtyFlagUrl;
}
