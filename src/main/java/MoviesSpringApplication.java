import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;


@SpringBootApplication
@EnableMongoAuditing
public class MoviesSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoviesSpringApplication.class, args);
	}


	/*@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
*/


/*
	@Bean
		CommandLineRunner run(UserService userService) {
			return  args -> {
				userService.saveRole(new UserRole(null,"USER"));
				userService.saveRole(new UserRole(null,"MANAGER"));
				userService.saveRole(new UserRole(null,"ADMIN"));
				userService.saveRole(new UserRole(null,"SUPER_ADMIN"));

				userService.saveUser(new AppUser(null,"John Travolta","john@gmail.com","password", new ArrayList<>()));
				userService.saveUser(new AppUser(null,"Will Smith","will@gmail.com","password", new ArrayList<>()));
				userService.saveUser(new AppUser(null,"Jim Carry","jim@gmail.com","password", new ArrayList<>()));
				userService.saveUser(new AppUser(null,"Arnold Schwarzenegger","arnold@gmail.com","password", new ArrayList<>()));

				userService.addRoleToUser("john@gmail.com","USER");
				userService.addRoleToUser("john@gmail.com","MANAGER");
				userService.addRoleToUser("will@gmail.com","MANAGER");
				userService.addRoleToUser("jim@gmail.com","ADMIN");
				userService.addRoleToUser("arnold@gmail.com","SUPER_MANAGER");
				userService.addRoleToUser("arnold@gmail.com","ADMIN");
				userService.addRoleToUser("arnold@gmail.com","USER");


			};
		}
	}*/
}

