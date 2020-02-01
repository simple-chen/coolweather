package com.coolweather.android.gson;
import com.google.gson.annotations.SerializedName;

public class Basic {
    /**
     * 因为JSON中的一些字段可能不太适合直接作为Java字段来命名,因为不好理解
     * 使用了@SerializedName注解的方式来让JSON字段和Java字段建立映射关系
     * 将JSON字段写在括号中，下一行写上自己需要用的命名
     */
    @SerializedName("city")
    public  String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public  class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
