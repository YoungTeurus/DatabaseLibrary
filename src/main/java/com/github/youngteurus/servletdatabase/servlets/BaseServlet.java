package com.github.youngteurus.servletdatabase.servlets;

import com.github.youngteurus.servletdatabase.models.error.ErrorMessage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public abstract class BaseServlet extends HttpServlet implements Servlet {
    private Map<String, String[]> requestParameters;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setAccessControlHeaders(resp);
        getRequestParameters(req);
        Object answer = processParameters();
        sendAnswer(answer, resp);
    }

    private void setAccessControlHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, X-Auth-Token");
    }

    protected abstract Object processParameters();

    private void sendAnswer(Object answer, HttpServletResponse resp) {
        try {
            if (answer == null){
                sendError(new ErrorMessage(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Внутренняя ошибка сервера: попытка отправить NULL-объект. Повторите попытку запроса позже."), resp);
            }
            // TODO: жуткая проверка, потом убрать.
            //  Засунуть проверку на тип в sendObject?
            if (answer instanceof ErrorMessage){
                sendError((ErrorMessage) answer, resp);
            } else {
                sendObject(answer, resp);
            }
        } catch (IOException ignored){
            System.out.println("BaseServlet::sendAnswer: произошла ошибка IOException при отправке ответа!");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setAccessControlHeaders(resp);
        getRequestParameters(req);
    }

    private void getRequestParameters(HttpServletRequest req){
        requestParameters = req.getParameterMap();
    }

    protected final String getRequestParameterValue(String name, String defaultValue){
        String returnValue = getRequestParameterValue(name);
        if (returnValue == null){
            return defaultValue;
        }
        return returnValue;
    }

    protected final String getRequestParameterValue(String name){
        String[] valuesArray = getRequestParameterValuesArray(name);
        if(valuesArray == null){
            return null;
        }
        return valuesArray[0];
    }

    protected final String[] getRequestParameterValuesArray(String name){
        if (requestParameters == null){
            throw new RuntimeException("UsersServlet::getRequestParameterValuesArray: requestParameters не инициализирован. Вызовите метод getRequestParameters перед получением параметра!");
        }

        @SuppressWarnings("UnnecessaryLocalVariable")
        String[] valuesArray = requestParameters.get(name);

        return valuesArray;
    }
}
