package com.yang.runbang.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.yang.runbang.R;
import com.yang.runbang.adapter.CityExpandableListAdapter;
import com.yang.runbang.adapter.CitylistViewAdapter;
import com.yang.runbang.db.DBManager;
import com.yang.runbang.model.bean.OLCity;
import com.yang.runbang.model.biz.ActivityManager;
import com.yang.runbang.utils.GeneralUtil;

import java.util.ArrayList;
import java.util.List;

public class OLMapActivity extends BaseActivity implements View.OnClickListener, MKOfflineMapListener {


    private ImageView bakImg;
    private EditText searchEdt;
    private TextView allText;
    private TextView downloadedText;

    private LinearLayout allLaout;

    private ExpandableListView expandableListView;
    private ListView searchListView;
    private ListView downloadedListview;


    private MKOfflineMap offlineMap = null; //离线地图服务

    private ArrayList<MKOLSearchRecord> cityList = null; //离线地图城市列表


    private ArrayList<MKOLSearchRecord> searchCityList = null;

    private ArrayList<MKOLUpdateElement> updateElements = null; // 更新信息


    private List<OLCity> data = new ArrayList<>(); //适配器数据

    private ArrayList<OLCity> searchData = new ArrayList<>(); //搜索数据

    private ArrayList<OLCity> downloadedData = new ArrayList<>(); //已下载数据

    private CitylistViewAdapter searchAdapter = null; //搜索listview适配器

    private CitylistViewAdapter downloadedAdapter = null; //已下载listview适配器

    private CityExpandableListAdapter adapter = null; //expandablelistView适配器


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ol_map);
        ActivityManager.getInstance().pushOneActivity(this);
        initComponent();

        //初始化离线地图
        initOfflineMap();

        // 获取数据
        getData();

        setAdapter();

        setsearchListener();


    }

    /**
     * 初始化组件
     */
    private void initComponent() {
        bakImg = (ImageView) findViewById(R.id.ol_map__back_img);
        allText = (TextView) findViewById(R.id.ol_map_toolbar_all);
        downloadedText = (TextView) findViewById(R.id.ol_map_toolbar_downloaded);
        searchEdt = (EditText) findViewById(R.id.ol_map_search_edt);

        expandableListView = (ExpandableListView) findViewById(R.id.ol_map_expandableListview);
        searchListView = (ListView) findViewById(R.id.ol_map_search_listview);
        downloadedListview = (ListView) findViewById(R.id.ol_map_download_listview);

        allLaout = (LinearLayout) findViewById(R.id.ol_map_all);

        expandableListView.setGroupIndicator(null);
        bakImg.setOnClickListener(this);
        allText.setOnClickListener(this);
        downloadedText.setOnClickListener(this);
    }
    /**
     * 初始化离线地图
     */
    private void initOfflineMap(){

        offlineMap = new MKOfflineMap();
        offlineMap.init(this);


    }
    /**
     * 适配数据
     */
    private void getData(){

        data = DBManager.getInstance(context).getAllOfflineCity();

        if(data == null || data.size() <= 0) {

            if(GeneralUtil.isNetworkAvailable(context)) {
                //获取离线地图城市列表
                cityList = offlineMap.getOfflineCityList();
                data = new ArrayList<>();
                //直辖市
                OLCity zhixiaCity = new OLCity();
                zhixiaCity.setCityName("直辖市");
                ArrayList<OLCity> zhixia = new ArrayList<>();
                OLCity beijing = new OLCity();
                searchCityList = offlineMap.searchCity("北京市");
                beijing.setCityName(searchCityList.get(0).cityName);
                beijing.setSize(searchCityList.get(0).size);
                beijing.setCityType(searchCityList.get(0).cityType);
                beijing.setCityID(searchCityList.get(0).cityID);
                zhixia.add(beijing);
                OLCity shanghai = new OLCity();
                searchCityList = null;
                searchCityList = offlineMap.searchCity("上海市");
                shanghai.setCityName(searchCityList.get(0).cityName);
                shanghai.setSize(searchCityList.get(0).size);
                shanghai.setCityType(searchCityList.get(0).cityType);
                shanghai.setCityID(searchCityList.get(0).cityID);
                zhixia.add(shanghai);
                OLCity tianjing = new OLCity();
                searchCityList = null;
                searchCityList = offlineMap.searchCity("天津市");
                tianjing.setCityName(searchCityList.get(0).cityName);
                tianjing.setSize(searchCityList.get(0).size);
                tianjing.setCityType(searchCityList.get(0).cityType);
                tianjing.setCityID(searchCityList.get(0).cityID);
                zhixia.add(tianjing);
                OLCity chongqing = new OLCity();
                searchCityList = null;
                searchCityList = offlineMap.searchCity("重庆市");
                chongqing.setCityName(searchCityList.get(0).cityName);
                chongqing.setSize(searchCityList.get(0).size);
                chongqing.setCityType(searchCityList.get(0).cityType);
                chongqing.setCityID(searchCityList.get(0).cityID);
                zhixia.add(chongqing);
                zhixiaCity.setChildCities(zhixia);
                data.add(zhixiaCity);

                //港澳
                OLCity gangao = new OLCity();
                gangao.setCityName("港澳");
                ArrayList<OLCity> gangaoList = new ArrayList<>();
                OLCity xianggang = new OLCity();
                searchCityList = null;
                searchCityList = offlineMap.searchCity("香港特别行政区");
                xianggang.setCityName(searchCityList.get(0).cityName);
                xianggang.setSize(searchCityList.get(0).size);
                xianggang.setCityType(searchCityList.get(0).cityType);
                xianggang.setCityID(searchCityList.get(0).cityID);
                gangaoList.add(xianggang);

                OLCity aomen = new OLCity();
                searchCityList = null;
                searchCityList = offlineMap.searchCity("澳门特别行政区");
                aomen.setCityName(searchCityList.get(0).cityName);
                aomen.setSize(searchCityList.get(0).size);
                aomen.setCityType(searchCityList.get(0).cityType);
                aomen.setCityID(searchCityList.get(0).cityID);
                gangaoList.add(aomen);

                gangao.setChildCities(gangaoList);

                data.add(gangao);

                if (cityList != null && cityList.size() > 0) {
                    for (MKOLSearchRecord city : cityList) {

                        //省份
                        if (city.cityType == 1) {
                            OLCity olCity = new OLCity();
                            olCity.setCityID(city.cityID);
                            olCity.setCityName(city.cityName);
                            olCity.setCityType(city.cityType);
                            olCity.setSize(city.size);

                            // 城市
                            ArrayList<OLCity> childlist = new ArrayList<>();
                            for (MKOLSearchRecord record : city.childCities) {
                                OLCity childCity = new OLCity();
                                childCity.setCityID(record.cityID);
                                childCity.setCityName(record.cityName);
                                childCity.setCityType(record.cityType);
                                childCity.setSize(record.size);
                                childlist.add(childCity);
                            }
                            olCity.setChildCities(childlist);
                            data.add(olCity);
                        }
                    }
                }
                DBManager.getInstance(context).insertOffline(data);
            }else {
                Toast.makeText(context,"请连接网络，下载离线地图",Toast.LENGTH_SHORT);
            }
        }
    }

    private void setAdapter() {

        if(data.size()>0) {
            adapter = new CityExpandableListAdapter(context,data);
            expandableListView.setAdapter(adapter);
        }
        updateView();

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                return false;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                String name = data.get(groupPosition).getChildCities().get(childPosition).getCityName();
                int cityid = data.get(groupPosition).getChildCities().get(childPosition).getCityID();

                int status = data.get(groupPosition).getChildCities().get(childPosition).getStatus();

                if (status == 0 || status == MKOLUpdateElement.SUSPENDED ||
                        status == MKOLUpdateElement.WAITING || status == MKOLUpdateElement.eOLDSFormatError) {
                    offlineMap.start(cityid);
                    Toast.makeText(context, name + "开始下载", Toast.LENGTH_SHORT).show();
                } else if (status == MKOLUpdateElement.DOWNLOADING) {
                    offlineMap.pause(cityid);
                    Toast.makeText(context, name + "暂停下载", Toast.LENGTH_SHORT).show();
                } else {
                    offlineMap.start(cityid);
                    Toast.makeText(context, name + "开始下载", Toast.LENGTH_SHORT).show();
                }
                updateView();
                return false;
            }
        });

    }

    /**
     * 更新视图
     */
    private void updateView(){
        ArrayList<MKOLUpdateElement> updateElements = offlineMap.getAllUpdateInfo();
        if (updateElements != null) {
            for(OLCity city :data) {

                for(OLCity childCity : city.getChildCities()){

                    for (MKOLUpdateElement element:updateElements) {
                        if(childCity.getCityID() == element.cityID) {
                            childCity.setRatio(element.ratio);
                            childCity.setStatus(element.status);
                            childCity.setUpdate(element.update);
                        }
                    }
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * 搜索城市
     */
    private void setsearchListener() {
        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String name = searchEdt.getText().toString();

                getSearchData(name);

                if(searchData.size()>0) {
                    expandableListView.setVisibility(View.GONE);
                    searchListView.setVisibility(View.VISIBLE);
                    setSearchAdapter();
                }else {
                    expandableListView.setVisibility(View.VISIBLE);
                    searchListView.setVisibility(View.GONE);
                }

            }
        });

    }

    /**
     * 获取搜索城市数据
     * @param name
     */
    private void getSearchData(String name){
        searchCityList = null;
        searchData.clear();
        searchCityList = offlineMap.searchCity(name);
        if (name != null && name.length() > 0 && searchCityList != null) {

            for (MKOLSearchRecord record : searchCityList) {

                for (OLCity city : data) {

                    for (OLCity childCity : city.getChildCities()) {

                        if (childCity.getCityID() == record.cityID) {

                            searchData.add(childCity);
                        }
                    }
                }
            }
        }
    }

    private void setSearchAdapter(){
        if(searchData.size()>0) {
            searchAdapter = new CitylistViewAdapter(context, searchData);
            searchListView.setAdapter(searchAdapter);
        }

        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = searchData.get(position).getCityName();
                int cityid = searchData.get(position).getCityID();
                int status =searchData.get(position).getStatus();

                if (status == 0 || status == MKOLUpdateElement.SUSPENDED ||
                        status == MKOLUpdateElement.WAITING || status == MKOLUpdateElement.eOLDSFormatError) {
                    offlineMap.start(cityid);
                    Toast.makeText(context,name+"开始下载",Toast.LENGTH_SHORT).show();
                } else if (status == MKOLUpdateElement.DOWNLOADING) {
                    offlineMap.pause(cityid);
                    Toast.makeText(context,name+"暂停下载",Toast.LENGTH_SHORT).show();
                }
                getSearchData(name);
                searchAdapter.notifyDataSetChanged();
            }
        });
    }
    /**
     * 获取已下载城市数据
     */
    private void getDownloadedData(){

        updateElements = null;
        downloadedData.clear();
        updateElements = offlineMap.getAllUpdateInfo();
        if (updateElements != null) {
            for(MKOLUpdateElement element:updateElements) {
                for (OLCity city : data) {
                    for (OLCity childCity : city.getChildCities()) {
                        if (childCity.getCityID() == element.cityID) {
                            downloadedData.add(childCity);
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置下载listview适配器，设置监听
     */
    private void setDownloadedAdapter() {
        if(downloadedData.size()>0) {
            downloadedAdapter = new CitylistViewAdapter(context, downloadedData);
            downloadedListview.setAdapter(downloadedAdapter);
        }

        downloadedListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = downloadedData.get(position).getCityName();
                int cityid = downloadedData.get(position).getCityID();
                int status =downloadedData.get(position).getStatus();

                if (status == 0 || status == MKOLUpdateElement.SUSPENDED ||
                        status == MKOLUpdateElement.WAITING || status == MKOLUpdateElement.eOLDSFormatError) {
                    offlineMap.start(cityid);
                    Toast.makeText(context,name+"开始下载",Toast.LENGTH_SHORT).show();
                } else if (status == MKOLUpdateElement.DOWNLOADING) {
                    offlineMap.pause(cityid);
                    Toast.makeText(context,name+"暂停下载",Toast.LENGTH_SHORT).show();
                }
                getDownloadedData();
                downloadedAdapter.notifyDataSetChanged();
            }
        });

        downloadedListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(OLMapActivity.this)
                        .setMessage("确定删除该离线地图")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //删除
                                offlineMap.remove(downloadedData.get(position).getCityID());
                                downloadedData.remove(position);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
                getDownloadedData();
                downloadedAdapter.notifyDataSetChanged();
                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch ( v.getId()) {
            case R.id.ol_map__back_img:
                this.finish();
                break;
            case R.id.ol_map_toolbar_all:
                downloadedListview.setVisibility(View.GONE);
                allLaout.setVisibility(View.VISIBLE);
                break;
            case R.id.ol_map_toolbar_downloaded:
                downloadedListview.setVisibility(View.VISIBLE);
                allLaout.setVisibility(View.GONE);

                getDownloadedData();
                setDownloadedAdapter();

                break;
        }
    }

    @Override
    protected void onDestroy() {
        offlineMap.destroy();
        super.onDestroy();
    }

    @Override
    public void onGetOfflineMapState(int i, int i1) {
        switch (i) {
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: //离线地图下载更新事件类型

                updateView();

//                getDownloadedData();
//                downloadedAdapter.notifyDataSetChanged();


                break;
            case MKOfflineMap.TYPE_NEW_OFFLINE: //新安装离线地图事件类型

                updateView();
                break;

            case MKOfflineMap.TYPE_VER_UPDATE : //离线地图数据版本更新事件类型

                break;
        }
    }
}
