package com.javarush;

import com.javarush.config.HibernateUtil;
import com.javarush.dao.*;
import com.javarush.domain.entity.*;
import com.javarush.domain.enums.Feature;
import com.javarush.domain.enums.Rating;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Customer customer = createCustomer();
        customerRentInventory(customer);
        returnInventoryToStore();
        createNewFilm();
    }

    private static void createNewFilm() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        LanguageDAO languageDAO = new LanguageDAO(sessionFactory);
        CategoryDAO categoryDAO = new CategoryDAO(sessionFactory);
        ActorDAO actorDAO = new ActorDAO(sessionFactory);
        FilmDAO filmDAO = new FilmDAO(sessionFactory);
        FilmTextDAO filmTextDAO = new FilmTextDAO(sessionFactory);

        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Language language = languageDAO.getItems(0,20).stream().unordered().findAny().get();
            List<Category> categories = categoryDAO.getItems(0, 5);
            List<Actor> actors = actorDAO.getItems(0, 20);

            Film film = new Film();
            film.setActors(new HashSet<>(actors));
            film.setRating(Rating.NC17);
            film.setSpecialFeatures(Set.of(Feature.TRAILERS,Feature.COMMENTARIES));
            film.setReplacementCost(BigDecimal.TEN);
            film.setRentalRate(BigDecimal.ZERO);
            film.setLanguage(language);
            film.setDescription("test description");
            film.setTitle("test film");
            film.setRentalDuration((byte) 44);
            film.setOriginalLanguage(language);
            film.setCategories(new HashSet<>(categories));
            film.setYear(Year.now());
            filmDAO.save(film);

            FilmText filmText = new FilmText();
            filmText.setFilm(film);
            filmText.setId(film.getId());
            filmText.setDescription("test description");
            filmText.setTitle("test film text");
            filmTextDAO.save(filmText);

            session.getTransaction().commit();
        }
    }

    private static void customerRentInventory(Customer customer) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        FilmDAO filmDAO = new FilmDAO(sessionFactory);
        InventoryDAO inventoryDAO = new InventoryDAO(sessionFactory);
        StoreDAO storeDAO = new StoreDAO(sessionFactory);
        RentalDAO rentalDAO = new RentalDAO(sessionFactory);
        PaymentDAO paymentDAO = new PaymentDAO(sessionFactory);

        try(Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
            session.beginTransaction();

            Film film = filmDAO.getFirstAvailableFilmRent();
            Store store = storeDAO.getItems(0, 1).get(0);

            Inventory inventory = new Inventory();
            inventory.setFilm(film);
            inventory.setStore(store);
            inventoryDAO.save(inventory);

            Staff staff = store.getStaff();

            Rental rental = new Rental();
            rental.setRentalDate(LocalDateTime.now());
            rental.setCustomer(customer);
            rental.setInventory(inventory);
            rental.setStaff(staff);
            rentalDAO.save(rental);

            Payment payment = new Payment();
            payment.setRental(rental);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setCustomer(customer);
            payment.setAmount(BigDecimal.valueOf(12.34));
            payment.setStaff(staff);
            paymentDAO.save(payment);

            session.getTransaction().commit();
        }
    }

    private static void returnInventoryToStore() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        RentalDAO rentalDAO = new RentalDAO(sessionFactory);

        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Rental rental = rentalDAO.getAnyUnreternedRental();
            rental.setReturnDate(LocalDateTime.now());

            rentalDAO.save(rental);

            session.getTransaction().commit();
        }
    }

    private static Customer createCustomer() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        StoreDAO storeDAO = new StoreDAO(sessionFactory);
        CityDAO cityDAO = new CityDAO(sessionFactory);
        AddressDAO addressDAO = new AddressDAO(sessionFactory);
        CustomerDAO customerDAO = new CustomerDAO(sessionFactory);

        try(Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Store store = storeDAO.getItems(0, 1).get(0);
            City city = cityDAO.getCityByName("Akron");

            Address address = new Address();
            address.setAddress("Urban str, 12");
            address.setPhone("111-222-333");
            address.setCity(city);
            address.setDistrict("test");
            addressDAO.save(address);

            Customer customer = new Customer();
            customer.setFirstName("FirstName");
            customer.setLastName("LastName");
            customer.setEmail("test@gmail.com");
            customer.setStore(store);
            customer.setIsActive(true);
            customer.setAddress(address);
            customerDAO.save(customer);

            session.getTransaction().commit();
            return customer;
        }
    }
}