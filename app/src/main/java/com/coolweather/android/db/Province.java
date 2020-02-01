package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 *
 * [{"id":1,"name":"北京","weather_id":"CN101010100"},
 * {"id":2,"name":"海淀","weather_id":"CN101010200"},
 * {"id":3,"name":"朝阳","weather_id":"CN101010300"},
 * {"id":4,"name":"顺义","weather_id":"CN101010400"},
 * {"id":5,"name":"怀柔","weather_id":"CN101010500"},
 * {"id":6,"name":"通州","weather_id":"CN101010600"},
 * {"id":7,"name":"昌平","weather_id":"CN101010700"},
 * 省略。。。] */
public class Province extends DataSupport {

    private int id ;
    private String provinceName;//省的名字
    private  int provinceCode;//省的代号

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
