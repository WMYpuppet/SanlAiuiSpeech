package com.example.administrator.sanlaiuispeech.fragment;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.sanlaiuispeech.activity.MainActivity;
import com.example.administrator.sanlaiuispeech.R;
import com.example.administrator.sanlaiuispeech.adapter.RSchemeAdapter;
import com.example.administrator.sanlaiuispeech.bean.InfoRScheme;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SchemeListFragment extends Fragment {


    private List<InfoRScheme> infoRSchemesList = new ArrayList<>();
    ListView listViewSet;
    RSchemeAdapter rSchemeAdapter;
    EditText et;
    View view;
    Boolean isSign = false;

    public SchemeListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_rscheme, container, false);
        init(view);
        rSchemeAdapter = new RSchemeAdapter(getContext(), R.layout.item_rscheme, infoRSchemesList);
        listViewSet.setAdapter(rSchemeAdapter);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {

        } else {
            if (isSign) {

            } else {
                showConfirmPsdDialog();
            }
        }
    }

    private void init(View view) {
        listViewSet = (ListView) view.findViewById(R.id.list_rscheme);
        InfoRScheme one = new InfoRScheme("赤铸山方案一", "2019-6-25");
        InfoRScheme two = new InfoRScheme("赤铸山方案二", "2019-6-25");
        InfoRScheme three = new InfoRScheme("赤铸山方案三", "2019-6-25");
        InfoRScheme four = new InfoRScheme("赤铸山方案四", "2019-6-25");
        infoRSchemesList.add(one);
        infoRSchemesList.add(two);
        infoRSchemesList.add(three);
        infoRSchemesList.add(four);

        showConfirmPsdDialog();
    }

    public void showConfirmPsdDialog() {
        //需要自己去定义对话框的显示样式，所以要调用dialog.setView(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final AlertDialog dialog = builder.create();
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm_psd, null);
        //让对话框显示一个自己定义的对话框界面效果
        dialog.setView(view);
        dialog.show();

        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
                String confirmPsd = et_confirm_psd.getText().toString();
                if (!TextUtils.isEmpty(confirmPsd)) {
                    if (confirmPsd.equals("123456")) {
                        listViewSet.setVisibility(View.VISIBLE);
                        isSign = true;
                        //跳转到新的界面以后需要去隐藏对话框
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "密码错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //提示用户密码输入为空的情况
                    Toast.makeText(getContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                MainActivity mainActivity3 = (MainActivity) getActivity();
                mainActivity3.replaceFragment(MainActivity.FRAGMENT_HOME);

            }
        });
    }
}
