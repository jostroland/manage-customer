package com.tansu.testcustomer.loaddatabase;

import com.tansu.testcustomer.dto.CustomerDto;
import com.tansu.testcustomer.mapper.CustomerMapper;
import com.tansu.testcustomer.repository.CustomerRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

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
        List<CustomerDto> customerDtos = IntStream.rangeClosed(1, 3)
                .mapToObj(
                        value -> CustomerDto.builder()
                                .firstName(faker.name().firstName())
                                .lastName(faker.name().firstName())
                                .age(faker.number().numberBetween(15,60))
                                .build()
                ).toList();

                customerDtos.stream()
                          .map(CustomerMapper::toEntity)
                            .forEach(customerRepository::save);

        return customerDtos.size();
    }


}
