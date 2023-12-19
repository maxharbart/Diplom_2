import ingredient.Ingredient;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import order.Order;
import order.OrderClient;
import order.OrderList;
import org.junit.After;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import user.User;
import user.UserClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;

@Epic("Создание заказа, получение списка заказов")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrderTest {
    Integer createdOrderNumber;

    @Test
    @DisplayName("Тест оформления заказа без ингредиентов авторизованным пользователем")
    public void newOrderWithLoginWithoutIngredientsTest() {
        String userAccessToken = userLoginAndGetAccessToken(User.getUserToSuccessLogin());
        Order order = new Order();
        Response orderResponse = OrderClient.sendPostRequestToOrdersWithToken(order, userAccessToken);
        orderResponse.then().assertThat().statusCode(400);
        orderResponse.then().assertThat().body("success", equalTo(false));
        orderResponse.then().assertThat().body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Тест оформления заказа без ингредиентов неавторизованным юзером")
    public void newOrderWithoutLoginWithoutIngredientsTest() {
        Order order = new Order();
        Response orderResponse = OrderClient.sendPostRequestToOrders(order);
        orderResponse.then().assertThat().statusCode(400);
        orderResponse.then().assertThat().body("success", equalTo(false));
        orderResponse.then().assertThat().body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Тест оформления заказа с неверным хешем ингредиентов")
    public void newOrderWithInvalidHashTest() {
        Order order = new Order();
        order.addIngredient(Ingredient.getIngredientWithInvalidHash());
        Response orderResponse = OrderClient.sendPostRequestToOrdersWithToken(order, userLoginAndGetAccessToken(User.getUserToSuccessLogin()));
        orderResponse.then().assertThat().statusCode(500);
        Assert.assertTrue(orderResponse.then().extract().htmlPath().getString("html.body.pre").contains("Internal Server Error"));
    }

    @Test
    @DisplayName("Тест получения заказов конкретного пользователя")
    public void getAllOrdersOfUserTest() {

        String userAccessToken = userLoginAndGetAccessToken(User.getUserToSuccessLogin());
        Response orderResponse = OrderClient.sendGetRequestToOrdersWithToken(userAccessToken);
        orderResponse.then().assertThat().statusCode(200);
        orderResponse.then().assertThat().body("success", equalTo(true));

        List<Order> ordersList = orderResponse.then().extract().body().path("orders");
        Assert.assertTrue(ordersList.size() <= 50);
    }

    @Test
    @DisplayName("Тест получения заказов")
    public void getAllOrdersListTest() {
        String userAccessToken = userLoginAndGetAccessToken(User.getUserToSuccessLogin());
        Response getAllOrdersResponse = OrderClient.sendGetRequestToOrdersAll(userAccessToken);
        getAllOrdersResponse.then().assertThat().statusCode(200);
        getAllOrdersResponse.then().assertThat().body("success", equalTo(true));
        Assert.assertTrue((Integer) getAllOrdersResponse.then().extract().body().path("total") > 0);
        Assert.assertTrue((Integer) getAllOrdersResponse.then().extract().body().path("totalToday") >= 0);

        OrderList returnedOrders = getAllOrdersResponse.getBody().as(OrderList.class);
        ArrayList<Order> orders = returnedOrders.getOrders();
        ArrayList<Order> ordersOrdered = (ArrayList<Order>) orders.clone();
        ordersOrdered.sort(ORDER_COMPARATOR);
        Assert.assertTrue(ordersOrdered.equals(orders));
    }

    @After
    public void tearDown(){
        if (createdOrderNumber != null)
            OrderClient.sendCancelRequestToOrders(createdOrderNumber);
    }

    private String userLoginAndGetAccessToken(User userToLogin) {
        Response userResponse = UserClient.sendPostRequestToUserLogin(userToLogin);
        userResponse.then().assertThat().statusCode(200);
        userResponse.then().assertThat().body("success", equalTo(true));
        return userResponse.then().extract().body().path("accessToken");
    }

    private static Comparator<Order> ORDER_COMPARATOR = new Comparator<Order>() {
        @Override
        public int compare(Order o1, Order o2) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                Date date1 = formatter.parse(o1.getUpdatedAt());
                Date date2 = formatter.parse(o2.getUpdatedAt());

                return date2.compareTo(date1);
            } catch (ParseException e) {
                System.out.println(e);
                throw new RuntimeException(e);
            }
        }
    };
}