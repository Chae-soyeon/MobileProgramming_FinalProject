package com.example.mobileprogramming_finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

public class MainActivity extends AppCompatActivity {

    private EditText foodEditText;
    private Button searchButton;
    private TextView resultTextView;

    private static final String API_KEY = "57622bb9449448fbba7f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        foodEditText = findViewById(R.id.foodEditText);
        searchButton = findViewById(R.id.searchButton);
        resultTextView = findViewById(R.id.resultTextView);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultTextView.setText("로딩 중...");
                resultTextView.setVisibility(View.VISIBLE);
                searchFood();
            }
        });
    }

    private void searchFood() {
        String foodName = foodEditText.getText().toString().trim();

        String requestUrl = "https://openapi.foodsafetykorea.go.kr/api/" + API_KEY + "/I2790/xml/1/5/DESC_KOR=" + foodName;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseXmlResponse(response);
                        resultTextView.setVisibility(View.VISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        resultTextView.setText("에러 발생: " + error.getMessage());
                        resultTextView.setVisibility(View.INVISIBLE);
                    }
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void parseXmlResponse(String xmlResponse) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlResponse));

            int eventType = parser.getEventType();
            String servingSize = "";
            String servingUnit = "";
            String calorie = "";
            String carbohydrate = "";
            String protein = "";
            String fat = "";
            String sugar = "";
            String sodium = "";
            String cholesterol = "";
            String saturatedFat = "";
            String transFat = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase("SERVING_SIZE")) {
                            servingSize = parser.nextText().trim();
                        } else if (tagName.equalsIgnoreCase("SERVING_UNIT")) {
                            servingUnit = parser.nextText().trim();
                        } else if (tagName.equalsIgnoreCase("NUTR_CONT1")) {
                            calorie = parser.nextText().trim();
                        } else if (tagName.equalsIgnoreCase("NUTR_CONT2")) {
                            carbohydrate = parser.nextText().trim();
                        } else if (tagName.equalsIgnoreCase("NUTR_CONT3")) {
                            protein = parser.nextText().trim();
                        } else if (tagName.equalsIgnoreCase("NUTR_CONT4")) {
                            fat = parser.nextText().trim();
                        } else if (tagName.equalsIgnoreCase("NUTR_CONT5")) {
                            sugar = parser.nextText().trim();
                        } else if (tagName.equalsIgnoreCase("NUTR_CONT6")) {
                            sodium = parser.nextText().trim();
                        } else if (tagName.equalsIgnoreCase("NUTR_CONT7")) {
                            cholesterol = parser.nextText().trim();
                        } else if (tagName.equalsIgnoreCase("NUTR_CONT8")) {
                            saturatedFat = parser.nextText().trim();
                        } else if (tagName.equalsIgnoreCase("NUTR_CONT9")) {
                            transFat = parser.nextText().trim();
                        }
                        break;
                }

                eventType = parser.next();
            }

            if (!calorie.isEmpty()) {
                String foodName = foodEditText.getText().toString().trim();
                resultTextView.setText("음식: " + foodName +
                        " (총내용량: " + servingSize + " " + servingUnit + ")\n" +
                        "\n칼로리: " + calorie + " kcal" +
                        "\n탄수화물: " + carbohydrate + "g" +
                        "\n단백질: " + protein + "g" +
                        "\n지방: " + fat + "g" +
                        "\n당류: " + sugar + "g" +
                        "\n나트륨: " + sodium + "mg" +
                        "\n콜레스테롤: " + cholesterol + "mg" +
                        "\n포화지방산: " + saturatedFat + "g" +
                        "\n트랜스지방: " + transFat + "g");
            } else {
                resultTextView.setText("칼로리 정보를 찾을 수 없습니다.");
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            resultTextView.setText("파싱 에러: " + e.getMessage());
        }
    }
}