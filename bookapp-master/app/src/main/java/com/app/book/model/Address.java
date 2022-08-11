package com.app.book.model;

public class Address {

    private Integer id;

    // 联系人
    private String contacts;
    // 手机
    private String mobile;
    // 省
    private String provinceName;
    // 市

    private String cityName;
    // 区
    private String districtName;
    // 详细地址
    private String address;

    // 默认地址
    private Integer dft;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getDft() {
        return dft;
    }

    public void setDft(Integer dft) {
        this.dft = dft;
    }
}
