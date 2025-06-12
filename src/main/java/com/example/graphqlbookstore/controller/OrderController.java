package com.example.graphqlbookstore.controller;

import com.example.graphqlbookstore.model.Book;
import com.example.graphqlbookstore.model.Order;
import com.example.graphqlbookstore.model.OrderItem;
import com.example.graphqlbookstore.model.User;
import com.example.graphqlbookstore.service.BookService;
import com.example.graphqlbookstore.service.OrderService;
import com.example.graphqlbookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final BookService bookService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService, BookService bookService) {
        this.orderService = orderService;
        this.userService = userService;
        this.bookService = bookService;
    }

    /**
     * GraphQL query to get all orders
     *
     * @return a list of all orders
     */
    @QueryMapping
    public List<Order> orders() {
        return orderService.getAllOrders();
    }

    /**
     * GraphQL query to get an order by ID
     *
     * @param id the ID of the order to find
     * @return the order if found, null otherwise
     */
    @QueryMapping
    public Order order(@Argument Long id) {
        return orderService.getOrderById(id).orElse(null);
    }

    /**
     * GraphQL query to get orders by user
     *
     * @param userId the ID of the user to find orders for
     * @return a list of orders for the given user
     */
    @QueryMapping
    public List<Order> ordersByUser(@Argument Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        return orderService.getOrdersByUser(user);
    }

    /**
     * GraphQL query to get orders by status
     *
     * @param status the status to search for
     * @return a list of orders with the given status
     */
    @QueryMapping
    public List<Order> ordersByStatus(@Argument Order.OrderStatus status) {
        return orderService.getOrdersByStatus(status);
    }

    /**
     * GraphQL mutation to create a new order
     *
     * @param input the input containing order details
     * @return the created order
     */
    @MutationMapping
    public Order createOrder(@Argument CreateOrderInput input) {
        User user = userService.getUserById(input.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + input.getUserId()));
        
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (OrderItemInput itemInput : input.getOrderItems()) {
            Book book = bookService.getBookById(itemInput.getBookId())
                    .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + itemInput.getBookId()));
            
            if (book.getStockQuantity() < itemInput.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for book: " + book.getTitle());
            }
            
            OrderItem orderItem = OrderItem.builder()
                    .book(book)
                    .quantity(itemInput.getQuantity())
                    .price(book.getPrice())
                    .build();
            
            orderItems.add(orderItem);
            totalAmount = totalAmount.add(book.getPrice().multiply(BigDecimal.valueOf(itemInput.getQuantity())));
        }
        
        Order order = Order.builder()
                .user(user)
                .status(Order.OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .shippingAddress(input.getShippingAddress())
                .billingAddress(input.getBillingAddress())
                .paymentMethod(input.getPaymentMethod())
                .build();
        
        for (OrderItem item : orderItems) {
            order.addOrderItem(item);
        }
        
        return orderService.createOrder(order);
    }

    /**
     * GraphQL mutation to update an order's status
     *
     * @param input the input containing order status details
     * @return the updated order if found, null otherwise
     */
    @MutationMapping
    public Order updateOrderStatus(@Argument UpdateOrderStatusInput input) {
        return orderService.updateOrderStatus(input.getId(), input.getStatus()).orElse(null);
    }

    /**
     * GraphQL mutation to update an order's shipping information
     *
     * @param input the input containing order shipping details
     * @return the updated order if found, null otherwise
     */
    @MutationMapping
    public Order updateOrderShipping(@Argument UpdateOrderShippingInput input) {
        return orderService.updateOrderShipping(input.getId(), input.getShippingAddress(), input.getTrackingNumber()).orElse(null);
    }

    /**
     * GraphQL mutation to add an item to an order
     *
     * @param input the input containing order item details
     * @return the updated order if found, null otherwise
     */
    @MutationMapping
    public Order addOrderItem(@Argument AddOrderItemInput input) {
        return orderService.addOrderItem(input.getOrderId(), input.getBookId(), input.getQuantity()).orElse(null);
    }

    /**
     * GraphQL mutation to remove an item from an order
     *
     * @param input the input containing order item details
     * @return the updated order if found, null otherwise
     */
    @MutationMapping
    public Order removeOrderItem(@Argument RemoveOrderItemInput input) {
        return orderService.removeOrderItem(input.getOrderId(), input.getOrderItemId()).orElse(null);
    }

    /**
     * GraphQL mutation to cancel an order
     *
     * @param id the ID of the order to cancel
     * @return the updated order if found, null otherwise
     */
    @MutationMapping
    public Order cancelOrder(@Argument Long id) {
        return orderService.cancelOrder(id).orElse(null);
    }

    /**
     * GraphQL mutation to delete an order
     *
     * @param id the ID of the order to delete
     * @return true if the order was deleted, false otherwise
     */
    @MutationMapping
    public boolean deleteOrder(@Argument Long id) {
        return orderService.deleteOrder(id);
    }

    /**
     * Input class for creating an order
     */
    public static class CreateOrderInput {
        private Long userId;
        private List<OrderItemInput> orderItems;
        private String shippingAddress;
        private String billingAddress;
        private String paymentMethod;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public List<OrderItemInput> getOrderItems() {
            return orderItems;
        }

        public void setOrderItems(List<OrderItemInput> orderItems) {
            this.orderItems = orderItems;
        }

        public String getShippingAddress() {
            return shippingAddress;
        }

        public void setShippingAddress(String shippingAddress) {
            this.shippingAddress = shippingAddress;
        }

        public String getBillingAddress() {
            return billingAddress;
        }

        public void setBillingAddress(String billingAddress) {
            this.billingAddress = billingAddress;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }
    }

    /**
     * Input class for order items in a new order
     */
    public static class OrderItemInput {
        private Long bookId;
        private Integer quantity;

        public Long getBookId() {
            return bookId;
        }

        public void setBookId(Long bookId) {
            this.bookId = bookId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }

    /**
     * Input class for updating an order status
     */
    public static class UpdateOrderStatusInput {
        private Long id;
        private Order.OrderStatus status;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Order.OrderStatus getStatus() {
            return status;
        }

        public void setStatus(Order.OrderStatus status) {
            this.status = status;
        }
    }

    /**
     * Input class for updating order shipping information
     */
    public static class UpdateOrderShippingInput {
        private Long id;
        private String shippingAddress;
        private String trackingNumber;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getShippingAddress() {
            return shippingAddress;
        }

        public void setShippingAddress(String shippingAddress) {
            this.shippingAddress = shippingAddress;
        }

        public String getTrackingNumber() {
            return trackingNumber;
        }

        public void setTrackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
        }
    }

    /**
     * Input class for adding an item to an order
     */
    public static class AddOrderItemInput {
        private Long orderId;
        private Long bookId;
        private Integer quantity;

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public Long getBookId() {
            return bookId;
        }

        public void setBookId(Long bookId) {
            this.bookId = bookId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }

    /**
     * Input class for removing an item from an order
     */
    public static class RemoveOrderItemInput {
        private Long orderId;
        private Long orderItemId;

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public Long getOrderItemId() {
            return orderItemId;
        }

        public void setOrderItemId(Long orderItemId) {
            this.orderItemId = orderItemId;
        }
    }
}
