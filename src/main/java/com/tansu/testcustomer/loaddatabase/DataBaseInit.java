package com.tansu.testcustomer.loaddatabase;

import com.tansu.testcustomer.dto.CustomerDto;
import com.tansu.testcustomer.enumeration.Sex;
import com.tansu.testcustomer.repository.CustomerRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;


@Component
public class DataBaseInit implements CommandLineRunner {

    private final Logger LOG = LoggerFactory.getLogger(DataBaseInit.class);

    private final CustomerRepository customerRepository;

    public DataBaseInit(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    private static boolean isRepositoryEmpty(JpaRepository repository) {
        return repository.count() <= 0;
    }

    @Override
    public void run(String... args) {
        LOG.info("-----------DEBUT INITIALISATION DE LA BASE DE DONNEE-----------");
        if(isRepositoryEmpty(customerRepository)){
            var numberOfCustomersLoaded = loadCustomers();
            LOG.info(String.format("%d  : Customers loaded in the database",numberOfCustomersLoaded));
        }
        LOG.info("-----------FIN INITIALISATION DE LA BASE DE DONNEE-----------");
    }

    private long loadCustomers(){
        var faker = new Faker();
        List<CustomerDto> customerDtos = IntStream.rangeClosed(1, 10)
                .mapToObj(
                        value -> CustomerDto.builder()
                                .firstName(faker.name().firstName())
                                .lastName(faker.name().firstName())
                                .sex(value % 2 == 0 ? Sex.FEMININE : Sex.MALE)
                                .email("email%d@yahoo.fr".formatted(value))
                                .build()
                ).toList();



                customerDtos.stream()
                          .map(CustomerDto::toEntity)
                            .forEach(customer -> {
                                customer.setCreatedAt(LocalDateTime.now());
                                customerRepository.save(customer);
                            });

        return customerDtos.size();
    }


}
