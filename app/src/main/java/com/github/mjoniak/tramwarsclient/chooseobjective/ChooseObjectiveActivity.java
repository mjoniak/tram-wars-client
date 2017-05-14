package com.github.mjoniak.tramwarsclient.chooseobjective;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mjoniak.tramwarsclient.ApplicationState;
import com.github.mjoniak.tramwarsclient.Const;
import com.github.mjoniak.tramwarsclient.R;
import com.github.mjoniak.tramwarsclient.datasource.ApiClient;
import com.github.mjoniak.tramwarsclient.datasource.IContinuation;
import com.github.mjoniak.tramwarsclient.datasource.IErrorHandler;
import com.github.mjoniak.tramwarsclient.datasource.dto.RouteDTO;
import com.github.mjoniak.tramwarsclient.domain.Objective;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChooseObjectiveActivity extends AppCompatActivity {

    private String accessToken;
    private List<String> objectiveExtras;

    private String currentStopName;
    private float currentLat;
    private float currentLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_objective);
        ListView listView = (ListView) findViewById(R.id.objectivesListView);
        objectiveExtras = getIntent().getStringArrayListExtra(Const.OBJECTIVES_EXTRA_KEY);
        List<String> names = new ArrayList<>();
        for (String serializedStop : objectiveExtras) {
            String[] parts = getPartsFromSerializedStop(serializedStop);
            String name = String.format(Locale.getDefault(), "%s (%s pkt)", parts[0], parts[3]);
            names.add(name);
        }
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names));
        accessToken = ApplicationState.getInstance().getAccessToken();
        String serializedCurrentStop = getIntent().getStringExtra(Const.POSITION_EXTRA_KEY);
        String[] currentStopParts = getPartsFromSerializedStop(serializedCurrentStop);
        currentStopName = currentStopParts[0];
        currentLat = Float.parseFloat(currentStopParts[1]);
        currentLng = Float.parseFloat(currentStopParts[2]);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChooseObjectiveActivity.this.onItemClick(position);
            }
        });
    }

    public void onItemClick(int position) {
        String item = objectiveExtras.get(position);
        String[] parts = getPartsFromSerializedStop(item);
        final String name = parts[0];
        final float lat = Float.parseFloat(parts[1]);
        final float lng = Float.parseFloat(parts[2]);
        ApiClient client = new ApiClient(ChooseObjectiveActivity.this, new IErrorHandler() {
            @Override
            public void handle(String message) {
                Toast.makeText(getApplicationContext(), R.string.connectivity_error, Toast.LENGTH_LONG).show();
                finish();
            }
        });
        client.postRoute(accessToken,
                currentStopName, currentLat, currentLng,
                name, lat, lng,
                new IContinuation<RouteDTO>() {
            @Override
            public void continueWith(RouteDTO response) {
                setObjective(response, name, lat, lng);
            }
        });
        finish();
    }

    private void setObjective(RouteDTO response, String name, float lat, float lng) {
        Resources res = getResources();
        String text = String.format(res.getString(R.string.route_started), name);
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
        ApplicationState state = ApplicationState.getInstance();
        state.setObjective(new Objective(new LatLng(lat, lng), response.getId()));
    }

    @NonNull
    private String[] getPartsFromSerializedStop(String serializedStop) {
        return serializedStop.split(",");
    }
}
