package com.example.colorviewerontemu.ui.grading;
import static android.view.View.INVISIBLE;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.colorviewerontemu.MainActivity;
import com.example.colorviewerontemu.R;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GradingFragment extends Fragment {

    ImageButton back;
    Button enter;
    EditText box;
    ImageView image;
    TextView textView;
    TextView process;

    String currentTest = "p1";
    int current = 1;
    int total = 1, both = 0, red = 0, green = 0, tri = 0, error = 0;

    List<String> red_and_green;
    List<String> red_or_green;
    List<String> vague_tritan;
    List<String> clear_tritan;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grading, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        process = rootView.findViewById(R.id.process);
        //back = rootView.findViewById(R.id.back);
        box = rootView.findViewById(R.id.box);
        enter = rootView.findViewById(R.id.enter);
        image = rootView.findViewById(R.id.imageView2);
        textView = rootView.findViewById(R.id.textView2);

        process.setText(current + " / " + 10);

//        back.setOnClickListener(v -> {
//            requireActivity().startActivity(new Intent(requireContext(), MainActivity.class));
//        });

        enter.setOnClickListener(v -> checkAnswer());

        red_and_green = new ArrayList<>(Arrays.asList("p2", "p3", "p4", "p5", "p6", "p7", "p8", "p9", "p10", "p11", "p12", "p13"));
        red_or_green = new ArrayList<>(Arrays.asList("p14", "p15", "p16", "p17"));
        vague_tritan = new ArrayList<>(Arrays.asList("p18", "p19", "p20", "p21", "p22"));
        clear_tritan = new ArrayList<>(Arrays.asList("p24", "p25"));

        //ImageButton startTutorialButton = rootView.findViewById(R.id.tutorial);
        //startTutorialButton.setOnClickListener(v -> startTutorial(rootView, image, box, enter, back));

        int[] buttonIds = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9
        };

        for (int id : buttonIds) {
            Button button = rootView.findViewById(id);
            button.setOnClickListener(this::onButtonClick);
        }

        Button delete = rootView.findViewById(R.id.buttonDelete);
        delete.setOnClickListener(this::onButtonClick);
    }

    private void onButtonClick(View view) {
        String currentText = box.getText().toString();

        if (view.getId() == R.id.buttonDelete) {
            if (!TextUtils.isEmpty(currentText)) {
                box.setText(currentText.substring(0, currentText.length() - 1));
            }
        } else if (view instanceof Button) {
            String value = ((Button) view).getText().toString();
            box.append(value);
        }
    }

    private void startTutorial(View rootView, View imageView, View box, View enterButton, View backButton) {
        new TapTargetSequence(requireActivity())
                .targets(
                        TapTarget.forView(imageView, "Test Image", "Observe the number visible in the image.")
                                .outerCircleColor(R.color.kkk)
                                .outerCircleAlpha(0.96f)
                                .transparentTarget(true)
                                .targetRadius(140)
                                .titleTextSize(20)
                                .descriptionTextSize(16)
                                .titleTextColor(android.R.color.white)
                                .descriptionTextColor(android.R.color.white)
                                .cancelable(true),

                        TapTarget.forView(box, "Enter the Number", "Input the number into this box.")
                                .outerCircleColor(R.color.kkk)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(android.R.color.white)
                                .transparentTarget(true)
                                .targetRadius(80)
                                .titleTextSize(20)
                                .descriptionTextSize(16)
                                .titleTextColor(android.R.color.white)
                                .descriptionTextColor(android.R.color.white)
                                .cancelable(true),

                        TapTarget.forView(enterButton, "Next Picture", "Click to proceed.")
                                .outerCircleColor(R.color.kkk)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(android.R.color.white)
                                .titleTextSize(20)
                                .descriptionTextSize(16)
                                .titleTextColor(android.R.color.white)
                                .descriptionTextColor(android.R.color.white)
                                .cancelable(true),

                        TapTarget.forView(backButton, "Back", "Click to return to main menu.")
                                .outerCircleColor(R.color.kkk)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(android.R.color.white)
                                .titleTextSize(20)
                                .descriptionTextSize(16)
                                .titleTextColor(android.R.color.white)
                                .descriptionTextColor(android.R.color.white)
                                .cancelable(true)
                )
                .start();
    }

    void switchTest() {
//        Toast.makeText(this, "total=" + total  + "both=" + both + " red="
//                + red + " green=" + green
//                + "error=" + error +  " tri=" + tri, Toast.LENGTH_SHORT).show();

        box.setText("");
        box.setHint("Enter number");
        if(total > 10) {
            return;
        }
        if(total == 10) {
            String result = "You do not have color blindness :D";
            if(error >= 7) result = "You might have total color blindness :(";
            else if(both >=3 && green >= 2) result = "You have Deuteranopia (green deficiency)";
            else if(both >=3 && red >= 2) result = "You have Protanopia (red deficiency)";
            else if(tri >=2 ) result = "You have Tritanopia (blue deficiency)";
            textView.setText(result);

            Resources res = getResources();
            int resID = res.getIdentifier("face" , "drawable", requireContext().getPackageName());
            image.setImageResource(resID);
            box.setVisibility(INVISIBLE);
            enter.setVisibility(INVISIBLE);
            return;
        }
        if(total<6) {
            setImg(1);
        }
        if(total >= 6) {
            if(both >=2) {
                setImg(2);
            }
            else {
                if(tri <= 2) setImg(3);
                else setImg(4);
            }
        }

        current++;
        total++;
        process.setText(current + " / " + 10); // Update progress
    }

    public void setImg(int i) {
        String mDrawableName = "p1";
        if(i==1) {
            mDrawableName = red_and_green.get(new Random().nextInt(red_and_green.size()));
            red_and_green.remove(mDrawableName);
        }
        if(i==2) {
            Log.d("test", red_or_green.size() + "");
            mDrawableName = red_or_green.get(new Random().nextInt(red_or_green.size()));
            red_or_green.remove(mDrawableName);
        }
        if(i==3) {
            mDrawableName = vague_tritan.get(new Random().nextInt(vague_tritan.size()));
            vague_tritan.remove(mDrawableName);
        }
        if(i==4) {
            mDrawableName = clear_tritan.get(new Random().nextInt(clear_tritan.size()));
            clear_tritan.remove(mDrawableName);
        }
        currentTest = mDrawableName;
        Resources res = getResources();
        int resID = res.getIdentifier(mDrawableName , "drawable", requireContext().getPackageName());
        image.setImageResource(resID);
    }

    void checkAnswer() {
        if(Objects.equals(currentTest, "p1")) {
            //right
            if(box.getText().toString().trim().equals("12")) {
                switchTest();
                return;
            }
            error++;
            switchTest();
        }

        else if(Objects.equals(currentTest, "p2")) {
            //right
            if(box.getText().toString().trim().equals("8")) {
                switchTest();
                return;
            }
            //red-green
            if(box.getText().toString().trim().equals("3")) {
                both++;
                switchTest();
                return;
            }
            error++;
            both++;
            switchTest();
        }

        else if(Objects.equals(currentTest, "p3")) {
            if(box.getText().toString().trim().equals("29")) {
                switchTest();
                return;
            }
            //red green
            if(box.getText().toString().trim().equals("70")) {
                both++;
                switchTest();
                return;
            }
            error++;
            both++;
            switchTest();
        }

        else if(Objects.equals(currentTest, "p4")) {
            //right
            if(box.getText().toString().trim().equals("5")) {
                switchTest();
                return;
            }
            //red-green
            if(box.getText().toString().trim().equals("2")) {
                both++;
                switchTest();
                return;
            }
            error++;
            both++;
            switchTest();
        }

        else if(Objects.equals(currentTest, "p5")) {
            //right
            if(box.getText().toString().trim().equals("3")) {
                switchTest();
                return;
            }
            //red-green
            if(box.getText().toString().trim().equals("5")) {
                both++;
                switchTest();
                return;
            }
            error++;
            both++;
            switchTest();
        }

        else if(Objects.equals(currentTest, "p6")) {
            if(box.getText().toString().trim().equals("15")) {
                switchTest();
                return;
            }
            //red green
            if(box.getText().toString().trim().equals("17")) {
                both++;
                switchTest();
                return;
            }
            error++;
            both++;
            switchTest();
        }

        else if(Objects.equals(currentTest, "p7")) {
            if(box.getText().toString().trim().equals("74")) {
                switchTest();
                return;
            }
            //red green
            if(box.getText().toString().trim().equals("21")) {
                both++;
                switchTest();
                return;
            }
            error++;
            both++;
            switchTest();
        }

        else if(Objects.equals(currentTest, "p8")) {
            if(box.getText().toString().trim().equals("6")) {
                switchTest();
                return;
            }
            //else color blind
            error++;
            both++;
            switchTest();
        }

        else if(Objects.equals(currentTest, "p9")) {
            if(box.getText().toString().trim().equals("45")) {
                switchTest();
                return;
            }
            //else...
            error++;
            both++;
            switchTest();
        }

        else if(Objects.equals(currentTest, "p10")) {
            if(box.getText().toString().trim().equals("5")) {
                switchTest();
                return;
            }
            //else...
            error++;
            both++;
            switchTest();
        }

        else if(Objects.equals(currentTest, "p11")) {
            if(box.getText().toString().trim().equals("7")) {
                switchTest();
                return;
            }
            //else...
            error++;
            both++;
            switchTest();
        }

        else if(Objects.equals(currentTest, "p12")) {
            if(box.getText().toString().trim().equals("16")) {
                switchTest();
                return;
            }
            //else...
            error++;
            both++;
            switchTest();
        }

        else if(Objects.equals(currentTest, "p13")) {
            if(box.getText().toString().trim().equals("73")) {
                switchTest();
                return;
            }
            //else...
            error++;
            both++;
            switchTest();
        }


        else if(Objects.equals(currentTest, "p14")) {
            if(box.getText().toString().trim().equals("26")) {
                switchTest();
                return;
            }
            //no red
            if(box.getText().toString().trim().equals("6")) {
                red++;
                switchTest();
                return;
            }
            //no green
            if(box.getText().toString().trim().equals("2")) {
                green++;
                switchTest();
                return;
            }
            error++;
            switchTest();
        }


        else if(Objects.equals(currentTest, "p15")) {
            if(box.getText().toString().trim().equals("42")) {
                switchTest();
                return;
            }
            //no red
            if(box.getText().toString().trim().equals("2")) {
                switchTest();
                red++;
                return;
            }
            //no green
            if(box.getText().toString().trim().equals("4")) {
                switchTest();
                green++;
                return;
            }
            error++;
            switchTest();
        }


        else if(Objects.equals(currentTest, "p16")) {
            if(box.getText().toString().trim().equals("35")) {
                switchTest();
                return;
            }
            //no red
            if(box.getText().toString().trim().equals("5")) {
                switchTest();
                red++;
                return;
            }
            //no green
            if(box.getText().toString().trim().equals("3")) {
                switchTest();
                green++;
                return;
            }
            error++;
            switchTest();
        }


        else if(Objects.equals(currentTest, "p17")) {
            if(box.getText().toString().trim().equals("96")) {
                switchTest();
                return;
            }
            //no red
            if(box.getText().toString().trim().equals("6")) {
                switchTest();
                red++;
                return;
            }
            //no green
            if(box.getText().toString().trim().equals("9")) {
                switchTest();
                green++;
                return;
            }
            error++;
            switchTest();
        }


        else if(Objects.equals(currentTest, "p18")) {
            if(box.getText().toString().trim().equals("97")) {
                switchTest();
                return;
            }
            //else tritan
            tri++;
            error++;
            switchTest();
        }


        else if(Objects.equals(currentTest, "p19")) {
            if(box.getText().toString().trim().equals("45")) {
                switchTest();
                return;
            }
            //else tritan
            tri++;
            error++;
            switchTest();
        }


        else if(Objects.equals(currentTest, "p20")) {
            if(box.getText().toString().trim().equals("16")) {
                switchTest();
                return;
            }
            //else tritan
            tri++;
            error++;
            switchTest();
        }


        else if(Objects.equals(currentTest, "p21")) {
            if(box.getText().toString().trim().equals("73")) {
                switchTest();
                return;
            }
            //else tritan
            tri++;
            error++;
            switchTest();
        }


        else if(Objects.equals(currentTest, "p22")) {
            if(box.getText().toString().trim().equals("29")) {
                switchTest();
                return;
            }
            //tritan
            if(box.getText().toString().trim().equals("70")) {
                switchTest();
                tri++;
                return;
            }
            error++;
            switchTest();
        }


        else if(Objects.equals(currentTest, "p23")) {
            if(box.getText().toString().trim().equals("57")) {
                switchTest();
                return;
            }
            //tritan
            if(box.getText().toString().trim().equals("55")) {
                switchTest();
                tri++;
                return;
            }
            error++;
            switchTest();
        }


        else if(Objects.equals(currentTest, "p24")) {
            if(box.getText().toString().trim().equals("15")) {
                switchTest();
                return;
            }

            //tritan
            if(box.getText().toString().trim().equals("17")) {
                switchTest();
                tri++;
                return;
            }
            error++;
            switchTest();
        }


        else if(Objects.equals(currentTest, "p25")) {
            if(box.getText().toString().trim().equals("74")) {
                switchTest();
                return;
            }

            //tritan
            if(box.getText().toString().trim().equals("21")) {
                switchTest();
                tri++;
                return;
            }
            error++;
            switchTest();
        }
    }
}
