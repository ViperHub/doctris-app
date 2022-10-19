package com.doctris.care.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.doctris.care.R;
import com.doctris.care.domain.ServiceResponse;
import com.doctris.care.entities.Category;
import com.doctris.care.entities.Service;
import com.doctris.care.repository.CategoryRepository;
import com.doctris.care.repository.ServiceRepository;
import com.doctris.care.ui.adapter.CategoryAdapter;
import com.doctris.care.ui.adapter.ServiceAdapter;

import java.util.ArrayList;
import java.util.List;

public class ServiceActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCategory;
    private RecyclerView recyclerViewService;
    private ProgressBar progressBar;
    private NestedScrollView nestedScrollView;
    private int page = 1;
    private final int limit = 10;
    private int totalPage = 0;

    private void bindingViews() {
        recyclerViewCategory = findViewById(R.id.recyclerview_category);
        recyclerViewService = findViewById(R.id.recyclerview_service);
        progressBar = findViewById(R.id.progressBar);
        nestedScrollView = findViewById(R.id.idNestedSV);
    }

    private void initLinearLayout() {
        LinearLayoutManager linearLayoutManagerHORIZONTAL = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategory.setLayoutManager(linearLayoutManagerHORIZONTAL);

        LiveData<List<Category>> categoryLiveData = CategoryRepository.getInstance().getCategories();
        categoryLiveData.observe(this, categories -> {
            if (categories != null) {
                CategoryAdapter categoryAdapter = new CategoryAdapter(categories, this);
                recyclerViewCategory.setAdapter(categoryAdapter);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewService.setLayoutManager(linearLayoutManager);

        List<Service> serviceList = new ArrayList<>();

        LiveData<ServiceResponse> serviceLiveData = ServiceRepository.getInstance().getServices(page, limit, null, null);
        serviceLiveData.observe(this, services -> {
            if (services != null) {
                totalPage = services.getTotalPages();
                serviceList.addAll(services.getItems());
                ServiceAdapter serviceAdapter = new ServiceAdapter(serviceList, this);
                recyclerViewService.setAdapter(serviceAdapter);
            }
        });

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                if(page < totalPage) {
                    page++;
                    progressBar.setVisibility(View.VISIBLE);
                    LiveData<ServiceResponse> serviceLiveData1 = ServiceRepository.getInstance().getServices(page, limit, null, null);
                    serviceLiveData1.observe(this, services -> {
                        if (services != null) {
                            totalPage = services.getTotalPages();
                            serviceList.addAll(services.getItems());
                            ServiceAdapter serviceAdapter = new ServiceAdapter(serviceList, this);
                            recyclerViewService.setAdapter(serviceAdapter);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        bindingViews();
        initLinearLayout();
    }
}