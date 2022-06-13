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
package com.sonicmsgr.util.encryption;

/**
 *
 * @author vithya
 */
public class SonicCryptLibPool {

    private int maxPool = 50;
    private final int MAXIMUM_POOL = 50;
    private SonicCryptLib[] sonicCryptLibPool = null;
    private int poolTrack = maxPool;
    private String key = "763f?werq[-kej";
    private String iv = "12xc?<irur@";
    private int poolResizeCount = 0;

    public SonicCryptLibPool(String _key, String _iv) {
        key = _key;
        iv = _iv;
        maxPool = 50;
        initPool(maxPool);
    }

    public SonicCryptLibPool(String password) {
        if ((password == null) || (password.trim().equals(""))) {
            return;
        }
        key = password;
        iv = key + "x!@*ccv";
        maxPool = 50;
        initPool(maxPool);
    }

    public SonicCryptLibPool(String password, int _maxPool) {
        if ((password == null) || (password.trim().equals(""))) {
            return;
        }
        key = password;
        iv = key + "x!@*ccv";
        maxPool = _maxPool;
        initPool(maxPool);
    }

    public SonicCryptLibPool(String _key, String _iv, int _maxPool) {
        key = _key;
        iv = _iv;
        maxPool = _maxPool;
        initPool(maxPool);
    }

    private void initPool(int size) {
        try {
            maxPool = size;

            sonicCryptLibPool = new SonicCryptLib[size];

            for (int i = 0; i < size; i++) {

                sonicCryptLibPool[i] = new SonicCryptLib(key, iv);
            }
        } catch (Exception e) {

        }
    }

    public SonicCryptLib getSonicCryptLibPool() {
        if ((poolResizeCount >= maxPool)||(sonicCryptLibPool==null)) {
            maxPool = maxPool + MAXIMUM_POOL;
            initPool(maxPool);
            poolResizeCount = 0;
        }

        if (poolTrack >= maxPool) {
            poolTrack = 0;
        }
        
        SonicCryptLib hm = sonicCryptLibPool[poolTrack];
        poolTrack++;
        poolIncrement();
        return hm;
    }

    public void resetPool() {
        poolTrack = 0;
        poolResizeCount = 0;
    }

    public void poolIncrement() {
        poolResizeCount++;
    }

    public void poolDecrement() {
        poolResizeCount--;
    }

//    public static void main(String args[]) {
////        
//        String key = "7614D14F808427056887B6C8DE569B373B81BB2D1D78945C58B261A390F2BE39";
//        String iv = key + "x!@*ccv";
////        
//
//        key = "7614D14F808427056887B6C8DE569B373B81BB2D1D78945C58B261A390F2BE39";
//        iv = "7614D14F808427056887B6C8DE569B373B81BB2D1D78945C58B261A390F2BE39x!@*ccv";
//        // String key = "7614D14F808427056887B6C8DE569B37";
//        //  String iv = "7614D14F80842705";
//
//        SonicCryptLibPool p = new SonicCryptLibPool(key, iv);
//        SonicCryptLib c = p.getSonicCryptLibPool();
//        try {
//            String e = c.encrypt("can you see it");
//            // System.out.println(e);
//            //   e = "gABhCzhq61hPXyXfwMIeAojF+K4vUdJtS77W78UqY3+r3CuDQvOE6t7o4u/b6JXJIB2kVk8KR0TdiLd6axhNZg==";
//
//            //  e = "/HAwGPV8pLTjTxAucaGv6tEzKJ73wWBJDfZyCtPl4A0XRZf5p0h3U9QNIq42s4tC3tYbWR88ruuR+nPD6RRGdRKgAJ0//dISN0JTlkWjXhjUqTo2zSEXW4z1VgQtYIkblB0paZ7ZTuUGTJJmULYIk+GPGMsoicV8MioLE+PsWTytXdoY8xAAsmX9//Xu/xGnPtu7NkBVoJjmjyYhv5K9GgugRUo1XhZiJINcCekfEhWYO/h/4ekhMgIycWc1v5SowHVzOdrgf7K2x70whs3QfJP28vBQ+piLDuxGUakWgFOoiePIXsWZudzfYqxAm7dd";
//            //  e = "9RYWp4+taw9Gh9x6T24XXW9nZz6MxtP2smhRlg3KXa+EC33Hamub/UWY7ZOsv5+vPLIF5gZHEJHksbHTvdtgXyJ+25Y+9JszV/zFl7swmziJw5TrDPkrFM38wsLqyQdnBIVyezMwndfZMxwGCgHlp9gPpWTDfMJrKHw+8PvIYMLpEqCpooYnC8JSI1rfxEGe96wgX8SnytqiKT6fZdg018LsQ6Uy1bKbm11ADuIedKNjeMqDkMihGhzAwPhXux8exhqBNeRljrroA5I6Ci0T2BeYaboilIt+7hzpRFhanKPhsgUr3Qi4jABkHdss7d/NhfYubSqIy8hWiQlLbM2g0p1PRK4xxYawu8nwnilSfsVfwVaw7x8cl7qfiAOA0NRWT4v318+nGD6/mPRnIRNYphiRFcpXyM9GuipvXE7+vaCO1NarmHfPKt13Ulz0ojWKUdQ1YWGgYhahstuHFGMiJqIJ1JDgfeAj2IFmP5ezqKMQ+ycGlj7VVZM80isq3Xc/YMZM6Rfe8AXAIETMi4fi0Vzi3gGcH5uic3xf1ofJzeM/XLvGEB4/e5psHVjjMbuzAxLBZDG84bYvgEX47mUsoqa2ul59Ov64XVm933DJsAR4VaxV0BH0cfa15uNNq9dQHzI4ymOpc92gh8Ol3Y9ThCJahW9+GvE5gM1+iRem4vSl+vVSFJgsEPx0yVSIDfA9XoUcrt12h91KuUQkvCaQC1X6nk4tNTGdKK5NkHECeTygvBVUVif5Cc1dhOZSEVJKUm1O86Oy9jskNt3t9N0tbWHzSJwl1WibMrL7HnPznvG9qudU/gdDkZJdhn6PRA0onFopW+OsgiZ5g+JELDWuabD2ZcxrOAeEjdFz/GGLPSvN15LTuWdUXG6AcefpkZod5L8UELSIGjpTg/a3UrDTvkexDYMPiR/LDJE8OzZpj9LQP7fATiA5+7yyUTtNPas2Mb1as7f031pYLVwA1R186WGNnnLGM0KB02Hzi2VP7ZXmGsrIikXOhQ7ZDX7ZDxXVRfbHQAk3IjWsHcaS5pueuTvT565+OP75XgN/87QqQUrTekLOWLXNlM7gSDV9nnVwkNgAV5HuCyz8Q9eI0uj5P2eH6g+/95vgq8p/xKDzjtv1t+cWs7I5johf7hs/ZzsILBhOl1eMWQlWideN2papD64oqqGqcj2Ull5xhW0PghtEdWSO5CY7Y6lTaZ9+3FW1ycwuE9lPf4P+0CR+/hljMJU59cNq/eAktXie7zdysLcka+6iejb13mO8NNVBBg4V8SjYN+f8nTsuC7fjpz+vlssykJjcyAkFmXc4/JFkf0iEV74N4WmYzaOjSaS5JcViY9Vx9PScuvf1Kvlk1iMs9Vo7eI/XwIneNXQqmJGeWxnio+U2zZuUW2b+PUu18Wg92crd4mFjhESAxwSxFkmkjtv82Rnyv2I1jiBumzIU3ouy6TGn2OGnq/25wGxJ120XBBnwQoMfnECJBLx3Rg6S8g==";
////           e="/HAwGPV8pLTjTxAucaGv6tEzKJ73wWBJDfZyCtPl4A0XRZf5p0h3U9QNIq42s4tC3tYbWR88ruuR+nPD6RRGdRKgAJ0//dISN0JTlkWjXhjUqTo2zSEXW4z1VgQtYIkblB0paZ7ZTuUGTJJmULYIk+GPGMsoicV8MioLE+PsWTytXdoY8xAAsmX9//Xu/xGnPtu7NkBVoJjmjyYhv5K9GgugRUo1XhZiJINcCekfEhWYO/h/4ekhMgIycWc1v5SowHVzOdrgf7K2x70whs3QfJP28vBQ+piLDuxGUakWgFOoiePIXsWZudzfYqxAm7dd";
////          
//            e = "gj5+VLwaSL29nVkH/n7PeQvr9ivLMq2TJCQH7FmGX2EFX40CC5hCd9AqR2GtdolwruUppJq85bKC/7fwX7tMJA==";
////           System.out.println(e);
////            
//            String d = c.decrypt(e);
//            System.out.println(d);
////             
//            //gRQWYBH4z6VY3idPh+e3tg== non
//            //tX85m87RXUwOOE9NGLz6Yw== is
//
//            // tBNdv7ZkWlaPrCQ5pcA6FQ== from flutter
//        } catch (Exception e) {
//
//            e.printStackTrace();
//
//        }
//    }
}
