package com.youyou.uucar.Utils.silent.handle;

import com.youyou.uucar.DB.Model.CarBrandModel;

import java.util.Comparator;


/**
 * @author Mr.Z
 */
public class PinyinComparator implements Comparator<CarBrandModel> {

    public int compare(CarBrandModel o1, CarBrandModel o2) {
        if (o2.getSortLetters().equals("任意")) {
            return 1;
        } else if (o1.getSortLetters().equals("@") || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#") || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }

}
