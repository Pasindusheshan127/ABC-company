package com.example.order.service;


import com.example.inventory.dto.InventoryDTO;
import com.example.order.common.ErrorOrderResponce;
import com.example.order.common.OrderResponce;
import com.example.order.common.SuccessOrderResponse;
import com.example.order.dto.OrderDTO;
import com.example.order.model.Order;
import com.example.order.repo.OrderRepo;
import com.example.product.dto.ProductDTO;
import jakarta.transaction.Transactional;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;


import java.util.List;

@Service
@Transactional
public class OrderService {

    private final WebClient inventoryWebClient;
    private final WebClient productWebClient;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ModelMapper modelMapper;

    public OrderService(@Qualifier("inventoryWebClient")WebClient inventoryWebClient,@Qualifier("productWebClient") WebClient productWebClient, OrderRepo orderRepo , ModelMapper modelMapper) {
        this.inventoryWebClient = inventoryWebClient;
        this.productWebClient = productWebClient;
        this.orderRepo = orderRepo;
        this.modelMapper = modelMapper;
    }

    public List<OrderDTO> getAllOrders() {
        List<Order> orderList = orderRepo.findAll();
        return modelMapper.map(orderList, new TypeToken<List<OrderDTO>>(){}.getType());
    }

    public OrderResponce saveOrder(OrderDTO orderDTO){

        Integer itemId = orderDTO.getItemId();

        try{
         InventoryDTO inventoryResponse = inventoryWebClient.get()
                 .uri(uriBuilder -> uriBuilder.path("/item/{itemId}").build(itemId))
                 .retrieve()
                 .bodyToMono(InventoryDTO.class)
                 .block();

            assert inventoryResponse != null;

            Integer productId = inventoryResponse.getProductId();

            ProductDTO productResponse = productWebClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/product/{productId}").build(productId))
                    .retrieve()
                    .bodyToMono(ProductDTO.class)
                    .block();

//            System.out.println(inventoryResponse);

            assert productResponse != null ;

            if(inventoryResponse.getQuantity() > 0){
                if(productResponse.getForSale() == 1){
                    orderRepo.save(modelMapper.map(orderDTO,Order.class));
                }else {
                    return new ErrorOrderResponce("This Item is not for sale");
                }

                return new SuccessOrderResponse(orderDTO);
            }else {
                return new ErrorOrderResponce("Item not available.plese try later");
            }
        } catch (WebClientResponseException e) {
            if(e.getStatusCode().is5xxServerError()){
                return new ErrorOrderResponce("Item not found");
            }

            e.printStackTrace();
        }

        return null;
    }

    public OrderDTO updateOrder(OrderDTO OrderDTO) {
        orderRepo.save(modelMapper.map(OrderDTO, Order.class));
        return OrderDTO;
    }

    public String deleteOrder(Integer orderId) {
        orderRepo.deleteById(orderId);
        return "Order deleted";
    }

    public OrderDTO getOrderById(Integer orderId) {
        Order order = orderRepo.getOrderById(orderId);
        return modelMapper.map(order, OrderDTO.class);
    }



}
