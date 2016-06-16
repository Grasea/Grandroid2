# Grandroid2

Grandroid2是一個開源的Android Library. 基於 [Grandroid](https://github.com/Grasea/Grandroid) 重新整合，更合理的拆分成多個Dependency，新版將更輕量化.

Grandroid2 is a open source library for Android. More stronger than [Grandroid](https://github.com/Grasea/Grandroid).

##功能 Features
**Grandroid2 包含Modules:**

 1. **Core**: 開發上常用之核心物件.

 2. **MVP-Framework(使用Annotation技術)**: 利用Annotation技術Presenter及View之間的setup更加快速及簡便.

 3. **Bluetooth LE Library(使用Annotation技術)**: 利用Annotation技術，快速實現BLE Deivce搜尋、裝置連接、監聽資料以及發送資料.

 4. **ORM Database**: 簡單易用的ORM Database library.

## 如何使用 Usage
1. Add Repository in build.gradle:



      repositories {
    	        maven { url 'https://dl.bintray.com/ch8154/maven' }
        }

2. Add dependency in buidle.gradle what library  you want.
###Core
引用了
1. com.android.support:appcompat-v7:23.3.0
2. com.android.support:recyclerview-v7:23.4.0


    dependencies {
	    compile 'com.grasea:grandroid-core:1.0.1'
    }
###MVP-Framework
引用了
1. com.grasea:grandroid-core:1.0.1
2. com.android.support:appcompat-v7:23.3.0
3. com.mcxiaoke.volley:library:1.0.+


    dependencies {
	    compile 'com.grasea:grandroid-mvp-framework:1.0.1-beta1'
    }
###Bluetooth LE Library
引用了
1. com.android.support:appcompat-v7:23.3.0


    dependencies {
	    compile 'com.grasea:grandroid-ble:1.0.0'
    }
###ORM Database
引用了
1. com.grasea:grandroid-core:1.0.1


    dependencies {
	    compile 'com.grasea:grandroid-database:2.0.0'
    }
