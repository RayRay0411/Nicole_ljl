package com.app.book;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

/*AppCompatActivity是继承自v4包的FragmentAvtivity，并且加入了很多新特性。这个可以很好的兼容老设备*/
public class MainActivity extends AppCompatActivity {

    @Override
    /*super就是调用父类的属性或者方法，反之this就是调用本类中的属性和方法*/
    /*onCreate方法的参数是一个Bundle类型的参数。Bundle类型的数据与Map类型的数据相似，都是以key-value的形式存储数据的*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);/*调用父类的onCreate构造函数*/
        /*savedInstanceState 是保存当前Activity的状态信息*/
        setContentView(R.layout.activity_main);/*首页*/
        /*setContentView就是设置一个Activity的显示界面，就是设置这个这句话所在的Activity采用R.layout下的activity_main布局文件【首页】进行布局*/
        BottomNavigationView navView = findViewById(R.id.nav_view);/*底部导航栏*/
        navView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_classify, R.id.navigation_cart, R.id.navigation_mine)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

}
