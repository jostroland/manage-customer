package com.tansu.testcustomer.loaddatabase;

import com.tansu.testcustomer.dto.CustomerDto;
import com.tansu.testcustomer.dto.UserRequest;
import com.tansu.testcustomer.entities.User;
import com.tansu.testcustomer.mapper.CustomerMapper;
import com.tansu.testcustomer.repository.CustomerRepository;
import com.tansu.testcustomer.repository.UserRepository;
import com.tansu.testcustomer.utils.Constants;
import lombok.val;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

import static com.tansu.testcustomer.utils.Constants.*;




@Component
@Profile({PROD, DEV})
public class DataBaseInit implements CommandLineRunner {

    private final Logger LOG = LoggerFactory.getLogger(DataBaseInit.class);
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataBaseInit(CustomerRepository customerRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

        if(isRepositoryEmpty(userRepository)){
            var numberOfUsersLoaded = loadUsers();
            LOG.info(String.format("%d  : Users loaded in the database",numberOfUsersLoaded));
        }

        LOG.info("-----------FIN INITIALISATION DE LA BASE DE DONNEE-----------");
    }

    private long loadCustomers(){
        var faker = new Faker();
        var customerDtos = IntStream.rangeClosed(1, 3)
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

    private long loadUsers(){
        val user = User.builder().name("user").email("user@gmail.com").password(passwordEncoder.encode("password")).roles("ROLE_USER").build();
        val u = userRepository.save(user);
        LOG.info("User : "+ u);
        LOG.info("userRepository.count() : "+ userRepository.count());
        return userRepository.count();
    }
}
