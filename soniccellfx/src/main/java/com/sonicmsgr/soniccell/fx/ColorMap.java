/*
 * Copyright 2018 Vithya Tith
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.ributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See t
 */
package com.sonicmsgr.soniccell.fx;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.paint.Color;
import static javafx.scene.paint.Color.BLUE;
import static javafx.scene.paint.Color.BLUEVIOLET;
import static javafx.scene.paint.Color.BROWN;
import static javafx.scene.paint.Color.CADETBLUE;
import static javafx.scene.paint.Color.CHARTREUSE;
import static javafx.scene.paint.Color.CHOCOLATE;
import static javafx.scene.paint.Color.CORAL;
import static javafx.scene.paint.Color.CORNFLOWERBLUE;
import static javafx.scene.paint.Color.CORNSILK;
import static javafx.scene.paint.Color.CRIMSON;
import static javafx.scene.paint.Color.CYAN;
import static javafx.scene.paint.Color.DARKBLUE;
import static javafx.scene.paint.Color.DARKCYAN;
import static javafx.scene.paint.Color.DARKGOLDENROD;
import static javafx.scene.paint.Color.DARKGRAY;
import static javafx.scene.paint.Color.DARKGREEN;
import static javafx.scene.paint.Color.DARKGREY;
import static javafx.scene.paint.Color.DARKKHAKI;
import static javafx.scene.paint.Color.DARKMAGENTA;
import static javafx.scene.paint.Color.DARKOLIVEGREEN;
import static javafx.scene.paint.Color.DARKORANGE;
import static javafx.scene.paint.Color.DARKORCHID;
import static javafx.scene.paint.Color.DARKRED;
import static javafx.scene.paint.Color.DARKSALMON;
import static javafx.scene.paint.Color.DARKSEAGREEN;
import static javafx.scene.paint.Color.DARKSLATEBLUE;
import static javafx.scene.paint.Color.DARKSLATEGRAY;
import static javafx.scene.paint.Color.DARKSLATEGREY;
import static javafx.scene.paint.Color.DARKTURQUOISE;
import static javafx.scene.paint.Color.DARKVIOLET;
import static javafx.scene.paint.Color.DEEPPINK;
import static javafx.scene.paint.Color.DEEPSKYBLUE;
import static javafx.scene.paint.Color.DIMGRAY;
import static javafx.scene.paint.Color.DIMGREY;
import static javafx.scene.paint.Color.DODGERBLUE;
import static javafx.scene.paint.Color.FIREBRICK;
import static javafx.scene.paint.Color.FLORALWHITE;
import static javafx.scene.paint.Color.FORESTGREEN;
import static javafx.scene.paint.Color.FUCHSIA;
import static javafx.scene.paint.Color.GAINSBORO;
import static javafx.scene.paint.Color.GHOSTWHITE;
import static javafx.scene.paint.Color.GOLD;
import static javafx.scene.paint.Color.GOLDENROD;
import static javafx.scene.paint.Color.GRAY;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.GREENYELLOW;
import static javafx.scene.paint.Color.GREY;
import static javafx.scene.paint.Color.HONEYDEW;
import static javafx.scene.paint.Color.HOTPINK;
import static javafx.scene.paint.Color.INDIANRED;
import static javafx.scene.paint.Color.INDIGO;
import static javafx.scene.paint.Color.IVORY;
import static javafx.scene.paint.Color.KHAKI;
import static javafx.scene.paint.Color.LAVENDER;
import static javafx.scene.paint.Color.LAVENDERBLUSH;
import static javafx.scene.paint.Color.LAWNGREEN;
import static javafx.scene.paint.Color.LEMONCHIFFON;
import static javafx.scene.paint.Color.LIGHTBLUE;
import static javafx.scene.paint.Color.LIGHTCORAL;
import static javafx.scene.paint.Color.LIGHTCYAN;
import static javafx.scene.paint.Color.LIGHTGOLDENRODYELLOW;
import static javafx.scene.paint.Color.LIGHTGRAY;
import static javafx.scene.paint.Color.LIGHTGREEN;
import static javafx.scene.paint.Color.LIGHTGREY;
import static javafx.scene.paint.Color.LIGHTPINK;
import static javafx.scene.paint.Color.LIGHTSALMON;
import static javafx.scene.paint.Color.LIGHTSEAGREEN;
import static javafx.scene.paint.Color.LIGHTSKYBLUE;
import static javafx.scene.paint.Color.LIGHTSLATEGRAY;
import static javafx.scene.paint.Color.LIGHTSLATEGREY;
import static javafx.scene.paint.Color.LIGHTSTEELBLUE;
import static javafx.scene.paint.Color.LIGHTYELLOW;
import static javafx.scene.paint.Color.LIME;
import static javafx.scene.paint.Color.LIMEGREEN;
import static javafx.scene.paint.Color.LINEN;
import static javafx.scene.paint.Color.MAGENTA;
import static javafx.scene.paint.Color.MAROON;
import static javafx.scene.paint.Color.MEDIUMAQUAMARINE;
import static javafx.scene.paint.Color.MEDIUMBLUE;
import static javafx.scene.paint.Color.MEDIUMORCHID;
import static javafx.scene.paint.Color.MEDIUMPURPLE;
import static javafx.scene.paint.Color.MEDIUMSEAGREEN;
import static javafx.scene.paint.Color.MEDIUMSLATEBLUE;
import static javafx.scene.paint.Color.MEDIUMSPRINGGREEN;
import static javafx.scene.paint.Color.MEDIUMTURQUOISE;
import static javafx.scene.paint.Color.MEDIUMVIOLETRED;
import static javafx.scene.paint.Color.MIDNIGHTBLUE;
import static javafx.scene.paint.Color.MINTCREAM;
import static javafx.scene.paint.Color.MISTYROSE;
import static javafx.scene.paint.Color.MOCCASIN;
import static javafx.scene.paint.Color.NAVAJOWHITE;
import static javafx.scene.paint.Color.NAVY;
import static javafx.scene.paint.Color.OLDLACE;
import static javafx.scene.paint.Color.OLIVE;
import static javafx.scene.paint.Color.OLIVEDRAB;
import static javafx.scene.paint.Color.ORANGE;
import static javafx.scene.paint.Color.ORANGERED;
import static javafx.scene.paint.Color.ORCHID;
import static javafx.scene.paint.Color.PALEGOLDENROD;
import static javafx.scene.paint.Color.PALEGREEN;
import static javafx.scene.paint.Color.PALETURQUOISE;
import static javafx.scene.paint.Color.PALEVIOLETRED;
import static javafx.scene.paint.Color.PAPAYAWHIP;
import static javafx.scene.paint.Color.PEACHPUFF;
import static javafx.scene.paint.Color.PERU;
import static javafx.scene.paint.Color.PINK;
import static javafx.scene.paint.Color.PLUM;
import static javafx.scene.paint.Color.POWDERBLUE;
import static javafx.scene.paint.Color.PURPLE;
import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.ROSYBROWN;
import static javafx.scene.paint.Color.ROYALBLUE;
import static javafx.scene.paint.Color.SADDLEBROWN;
import static javafx.scene.paint.Color.SALMON;
import static javafx.scene.paint.Color.SANDYBROWN;
import static javafx.scene.paint.Color.SEAGREEN;
import static javafx.scene.paint.Color.SEASHELL;
import static javafx.scene.paint.Color.SIENNA;
import static javafx.scene.paint.Color.SILVER;
import static javafx.scene.paint.Color.SKYBLUE;
import static javafx.scene.paint.Color.SLATEBLUE;
import static javafx.scene.paint.Color.SLATEGRAY;
import static javafx.scene.paint.Color.SLATEGREY;
import static javafx.scene.paint.Color.SNOW;
import static javafx.scene.paint.Color.SPRINGGREEN;
import static javafx.scene.paint.Color.STEELBLUE;
import static javafx.scene.paint.Color.TAN;
import static javafx.scene.paint.Color.TEAL;
import static javafx.scene.paint.Color.THISTLE;
import static javafx.scene.paint.Color.TOMATO;
import static javafx.scene.paint.Color.TRANSPARENT;
import static javafx.scene.paint.Color.TURQUOISE;
import static javafx.scene.paint.Color.VIOLET;
import static javafx.scene.paint.Color.WHEAT;
import static javafx.scene.paint.Color.WHITE;
import static javafx.scene.paint.Color.WHITESMOKE;
import static javafx.scene.paint.Color.YELLOW;
import static javafx.scene.paint.Color.YELLOWGREEN;

/**
 *
 * @author yada
 */
public class ColorMap {

    private static ColorMap colorMap = null;
    private Map<String, Color> colorDB = new HashMap<String, Color>(256);
    private Map<String,Color> colorList = new HashMap<String,Color>();
    private ColorMap(){
        
    }
    
    public Color getColor(String typeName){
        
        if(typeName==null){
            return Color.BLACK;
        }
        String name = typeName.toLowerCase();
        if(name.equals("")){
            return Color.BLACK;
        }
        if(colorList.containsKey(name)){
            return colorList.get(name);
        }else{
            int size = colorList.size();
            
            Color color = colorDB.get(Integer.toString(size));
            colorList.put(name, color);
            
            return color;
        }
    }
    
    public static ColorMap getIntance(){
        
        if(colorMap==null){
            colorMap = new ColorMap();
            colorMap.init();
        }
        return colorMap;
    }
    
    
    private void init() {
        //Red	Orange	Yellow	Green	Blue	Indigo	Violet
    	    	    	    	    	    	    

        colorDB.put("0", ORANGE);
        colorDB.put("1", BLUE);
        colorDB.put("2", INDIGO);
        colorDB.put("3", VIOLET);
        colorDB.put("4", BROWN);
        colorDB.put("5", PINK);
        colorDB.put("6", NAVY);
        colorDB.put("7", GOLD);
        colorDB.put("8", MAGENTA);
        colorDB.put("9", TURQUOISE);
        colorDB.put("10", BLUEVIOLET);
        colorDB.put("11", YELLOWGREEN);
        colorDB.put("12", LIGHTCORAL);
        colorDB.put("13", CADETBLUE);
        colorDB.put("14", CHARTREUSE);
        colorDB.put("15", CHOCOLATE);
        colorDB.put("16", CORAL);
        colorDB.put("17", CORNFLOWERBLUE);
        colorDB.put("18", CORNSILK);
        colorDB.put("19", CRIMSON);
        colorDB.put("20", CYAN);
        colorDB.put("21", DARKBLUE);
        colorDB.put("22", DARKCYAN);
        colorDB.put("23", DARKGOLDENROD);
        colorDB.put("24", DARKGRAY);
        colorDB.put("25", DARKGREEN);
        colorDB.put("26", DARKGREY);
        colorDB.put("27", DARKKHAKI);
        colorDB.put("28", DARKMAGENTA);
        colorDB.put("29", DARKOLIVEGREEN);
        colorDB.put("30", DARKORANGE);
        colorDB.put("31", DARKORCHID);
        colorDB.put("32", DARKRED);
        colorDB.put("33", DARKSALMON);
        colorDB.put("34", DARKSEAGREEN);
        colorDB.put("35", DARKSLATEBLUE);
        colorDB.put("36", DARKSLATEGRAY);
        colorDB.put("37", DARKSLATEGREY);
        colorDB.put("38", DARKTURQUOISE);
        colorDB.put("39", DARKVIOLET);
        colorDB.put("40", DEEPPINK);
        colorDB.put("41", DEEPSKYBLUE);
        colorDB.put("42", DIMGRAY);
        colorDB.put("43", DIMGREY);
        colorDB.put("44", DODGERBLUE);
        colorDB.put("45", FIREBRICK);
        colorDB.put("46", FLORALWHITE);
        colorDB.put("45", FORESTGREEN);
        colorDB.put("46", FUCHSIA);
        colorDB.put("47", GAINSBORO);
        colorDB.put("48", GHOSTWHITE);
        colorDB.put("49", GOLD);
        colorDB.put("50", GOLDENROD);
        colorDB.put("51", GRAY);
        colorDB.put("52", GREEN);
        colorDB.put("53", GREENYELLOW);
        colorDB.put("grey", GREY);
        colorDB.put("honeydew", HONEYDEW);
        colorDB.put("hotpink", HOTPINK);
        colorDB.put("indianred", INDIANRED);
        colorDB.put("indigo", INDIGO);
        colorDB.put("ivory", IVORY);
        colorDB.put("khaki", KHAKI);
        colorDB.put("lavender", LAVENDER);
        colorDB.put("lavenderblush", LAVENDERBLUSH);
        colorDB.put("lawngreen", LAWNGREEN);
        colorDB.put("lemonchiffon", LEMONCHIFFON);
        colorDB.put("lightblue", LIGHTBLUE);
        colorDB.put("lightcoral", LIGHTCORAL);
        colorDB.put("lightcyan", LIGHTCYAN);
        colorDB.put("lightgoldenrodyellow", LIGHTGOLDENRODYELLOW);
        colorDB.put("lightgray", LIGHTGRAY);
        colorDB.put("lightgreen", LIGHTGREEN);
        colorDB.put("lightgrey", LIGHTGREY);
        colorDB.put("lightpink", LIGHTPINK);
        colorDB.put("lightsalmon", LIGHTSALMON);
        colorDB.put("lightseagreen", LIGHTSEAGREEN);
        colorDB.put("lightskyblue", LIGHTSKYBLUE);
        colorDB.put("lightslategray", LIGHTSLATEGRAY);
        colorDB.put("lightslategrey", LIGHTSLATEGREY);
        colorDB.put("lightsteelblue", LIGHTSTEELBLUE);
        colorDB.put("lightyellow", LIGHTYELLOW);
        colorDB.put("lime", LIME);
        colorDB.put("limegreen", LIMEGREEN);
        colorDB.put("linen", LINEN);
        colorDB.put("magenta", MAGENTA);
        colorDB.put("maroon", MAROON);
        colorDB.put("mediumaquamarine", MEDIUMAQUAMARINE);
        colorDB.put("mediumblue", MEDIUMBLUE);
        colorDB.put("mediumorchid", MEDIUMORCHID);
        colorDB.put("mediumpurple", MEDIUMPURPLE);
        colorDB.put("mediumseagreen", MEDIUMSEAGREEN);
        colorDB.put("mediumslateblue", MEDIUMSLATEBLUE);
        colorDB.put("mediumspringgreen", MEDIUMSPRINGGREEN);
        colorDB.put("mediumturquoise", MEDIUMTURQUOISE);
        colorDB.put("mediumvioletred", MEDIUMVIOLETRED);
        colorDB.put("midnightblue", MIDNIGHTBLUE);
        colorDB.put("mintcream", MINTCREAM);
        colorDB.put("mistyrose", MISTYROSE);
        colorDB.put("moccasin", MOCCASIN);
        colorDB.put("navajowhite", NAVAJOWHITE);
        colorDB.put("navy", NAVY);
        colorDB.put("oldlace", OLDLACE);
        colorDB.put("olive", OLIVE);
        colorDB.put("olivedrab", OLIVEDRAB);
        colorDB.put("orange", ORANGE);
        colorDB.put("orangered", ORANGERED);
        colorDB.put("orchid", ORCHID);
        colorDB.put("palegoldenrod", PALEGOLDENROD);
        colorDB.put("palegreen", PALEGREEN);
        colorDB.put("paleturquoise", PALETURQUOISE);
        colorDB.put("palevioletred", PALEVIOLETRED);
        colorDB.put("papayawhip", PAPAYAWHIP);
        colorDB.put("peachpuff", PEACHPUFF);
        colorDB.put("peru", PERU);
        colorDB.put("pink", PINK);
        colorDB.put("plum", PLUM);
        colorDB.put("powderblue", POWDERBLUE);
        colorDB.put("purple", PURPLE);
        colorDB.put("red", RED);
        colorDB.put("rosybrown", ROSYBROWN);
        colorDB.put("royalblue", ROYALBLUE);
        colorDB.put("saddlebrown", SADDLEBROWN);
        colorDB.put("salmon", SALMON);
        colorDB.put("sandybrown", SANDYBROWN);
        colorDB.put("seagreen", SEAGREEN);
        colorDB.put("seashell", SEASHELL);
        colorDB.put("sienna", SIENNA);
        colorDB.put("silver", SILVER);
        colorDB.put("skyblue", SKYBLUE);
        colorDB.put("slateblue", SLATEBLUE);
        colorDB.put("slategray", SLATEGRAY);
        colorDB.put("slategrey", SLATEGREY);
        colorDB.put("snow", SNOW);
        colorDB.put("springgreen", SPRINGGREEN);
        colorDB.put("steelblue", STEELBLUE);
        colorDB.put("tan", TAN);
        colorDB.put("teal", TEAL);
        colorDB.put("thistle", THISTLE);
        colorDB.put("tomato", TOMATO);
        colorDB.put("transparent", TRANSPARENT);
        colorDB.put("turquoise", TURQUOISE);
        colorDB.put("violet", VIOLET);
        colorDB.put("wheat", WHEAT);
        colorDB.put("white", WHITE);
        colorDB.put("whitesmoke", WHITESMOKE);
        colorDB.put("yellow", YELLOW);
        colorDB.put("yellowgreen", YELLOWGREEN);
    }
}
