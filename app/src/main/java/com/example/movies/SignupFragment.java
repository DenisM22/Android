package com.example.movies;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class SignupFragment extends Fragment {

    private DatabaseHandler db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sign_up, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = new DatabaseHandler(requireContext());

        EditText usernameField = view.findViewById(R.id.username2);
        EditText passwordField = view.findViewById(R.id.password2);
        EditText passwordField2 = view.findViewById(R.id.password3);

        Button signupButton = view.findViewById(R.id.sign_up2);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString().trim();
                String password = passwordField.getText().toString();
                String password2 = passwordField2.getText().toString();

                if (isInputValid(username, password, password2)) {
                    if (db.addUser(username, password)) {
                        AppConfig.user = db.getUser(username);
                        AppConfig.loginName = username;

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        getActivity().finish();

                        Toast.makeText(getActivity(), "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                    } else
                            Toast.makeText(getActivity(), "Ошибка при регистрации", Toast.LENGTH_SHORT).show();

                    }

            }
        });

    }


    private boolean isInputValid(String username, String password, String password2) {

        if (username.isEmpty() || password.isEmpty() || password2.isEmpty()) {
            Toast.makeText(getActivity(), "Все поля обязательны для заполнения", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (db.checkUser(username)) {
            Toast.makeText(getActivity(), "Пользователь с именем " + username + " уже существует", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Pattern.compile("\\w+").matcher(username).matches()) {
            Toast.makeText(getActivity(), "Имя пользователя содержит недопустимые символы", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Pattern.compile("[a-zA-Z]+\\w+").matcher(username).matches()) {
            Toast.makeText(getActivity(), "Имя пользователя должно начинаться с буквы", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Pattern.compile("[a-zA-Z]+\\w{3,20}").matcher(username).matches()) {
            Toast.makeText(getActivity(), "Имя пользователя не должно быть меньше 3 и больше 20 символов", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Pattern.compile("\\w+").matcher(password).matches()) {
            Toast.makeText(getActivity(), "Пароль содержит недопустимые символы", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Pattern.compile("\\w{4,12}").matcher(password).matches()) {
            Toast.makeText(getActivity(), "Пароль не должен быть меньше 4 и больше 12 символов", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(password2)) {
            Toast.makeText(getActivity(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


}
