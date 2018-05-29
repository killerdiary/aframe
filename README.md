# aframe
#### Android Frame

#### 使用方式

~~~
dependencies{
    implementation 'com.github.killerdiary:aframe:v2.3.1'
}
~~~


### 其他引用包

~~~
dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jre7:$kotlinVersion"
    implementation "com.android.support:design:$supportVersion"
    implementation "com.android.support.constraint:constraint-layout:$constraintVersion"
    implementation ("com.github.bumptech.glide:glide:$glideVersion"){
        exclude group: 'com.android.support'
    }
    implementation "com.squareup.okhttp3:okhttp:$okhttpVersion"
    implementation "com.google.code.gson:gson:$gsonVersion"
    implementation 'io.reactivex.rxjava2:rxjava:2.1.9'
    implementation ('io.reactivex.rxjava2:rxandroid:2.0.2'){
        exclude group: 'io.reactivex.rxjava2', module: 'rxjava'
    }
    implementation ('com.squareup.retrofit2:retrofit:2.4.0') {
        exclude group: 'com.android.support'
        exclude group: 'com.squareup.okhttp3'
    }
    implementation ('com.squareup.retrofit2:adapter-rxjava2:2.4.0'){
        exclude group: 'com.squareup.retrofit2', module: 'retrofit'
        exclude group: 'io.reactivex.rxjava2', module: 'rxjava'
    }
    implementation ('com.squareup.retrofit2:converter-gson:2.4.0'){
        exclude group: 'com.squareup.retrofit2', module: 'retrofit'
        exclude group: 'com.google.code.gson', module: 'gson'
    }
    implementation("com.github.chrisbanes:PhotoView:$photoviewVersion") {
        exclude group: 'com.android.support'
    }
    implementation('com.github.LuckSiege.PictureSelector:picture_library:v2.2.2') {
        exclude group: 'com.android.support'
        exclude group: 'io.reactivex.rxjava2'
        exclude group: 'com.github.bumptech.glide'
    }
    implementation 'com.scwang.smartrefresh:SmartRefreshLayout:1.1.0-alpha-5'
    implementation 'com.android.support:multidex:1.0.3'
}
~~~
