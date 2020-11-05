package com.sample.payloads;


/**
 * Класс для приёма полезной нагрузки запроса на авторизацию/регистрацию.
 * Позволяет принять json-объект с ключами username и password в теле запроса.
 */
public class Login
{

    private String username;
    private String password;
        /*Login(String username, String password){
            this.password = password;
            this.username = username;
        }*/

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }

    public String getUsername()
    {
        return username;
    }
}