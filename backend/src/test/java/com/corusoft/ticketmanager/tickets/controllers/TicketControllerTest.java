package com.corusoft.ticketmanager.tickets.controllers;

import com.corusoft.ticketmanager.TestUtils;
import com.corusoft.ticketmanager.common.dtos.GenericValueDTO;
import com.corusoft.ticketmanager.common.jwt.JwtData;
import com.corusoft.ticketmanager.common.jwt.JwtGenerator;
import com.corusoft.ticketmanager.tickets.controllers.dtos.*;
import com.corusoft.ticketmanager.tickets.controllers.dtos.conversors.CategoryConversor;
import com.corusoft.ticketmanager.tickets.controllers.dtos.conversors.TicketConversor;
import com.corusoft.ticketmanager.tickets.controllers.dtos.filters.TicketFilterParamsDTO;
import com.corusoft.ticketmanager.tickets.entities.*;
import com.corusoft.ticketmanager.tickets.repositories.*;
import com.corusoft.ticketmanager.tickets.services.TicketService;
import com.corusoft.ticketmanager.users.controllers.dtos.AuthenticatedUserDTO;
import com.corusoft.ticketmanager.users.entities.User;
import com.corusoft.ticketmanager.users.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.corusoft.ticketmanager.common.security.JwtFilter.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TicketControllerTest {
    /* ************************* CONSTANTES ************************* */
    private static final String API_ENDPOINT = "/api/tickets";
    private final Locale locale = Locale.getDefault();


    /* ************************* DEPENDENCIAS ************************* */
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private JwtGenerator jwtGenerator;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CustomizedCategoryRepository customizedCategoryRepository;
    @Autowired
    private TicketService ticketService;


    /* ************************* MÉTODOS AUXILIARES ************************* */


    /* ************************* CICLO VIDA TESTS ************************* */
    @BeforeEach
    void beforeEach() {
        // Limpiar datos guardados de otros test

    }


    /* ************************* CASOS DE PRUEBA ************************* */

    @Test
    void whenCreateCustomizedCategory_thenCustomizedCategoryDTO() throws Exception {
        // Crear datos de prueba
        User validUser = testUtils.generateValidUser();
        AuthenticatedUserDTO authUserDTO = testUtils.generateAuthenticatedUser(validUser);      // Registra un usuario y obtiene el DTO respuesta
        JwtData jwtData = jwtGenerator.extractInfoFromToken(authUserDTO.getServiceToken());
        Category validCategory = testUtils.registerValidCategory();
        CreateCustomizedCategoryParamsDTO paramsDTO = new CreateCustomizedCategoryParamsDTO();
        paramsDTO.setName(validCategory.getName());
        paramsDTO.setMaxWasteLimit(testUtils.DEFAULT_CATEGORY_MAX_WASTE_LIMIT);

        CustomizedCategoryDTO customizedCategoryDTO = new CustomizedCategoryDTO();
        customizedCategoryDTO.setId(new CustomizedCategoryID(validUser.getId(), validCategory.getId()));
        customizedCategoryDTO.setName(validCategory.getName());
        customizedCategoryDTO.setMaxWasteLimit(testUtils.DEFAULT_CATEGORY_MAX_WASTE_LIMIT);

        // Ejecutar funcionalidades
        String endpoint = API_ENDPOINT + "/categories";
        String encodedBodyContent = this.jsonMapper.writeValueAsString(paramsDTO);
        String expectedContent = this.jsonMapper.writeValueAsString(customizedCategoryDTO);
        ResultActions actions = mockMvc.perform(
            post(endpoint)
                // Valores anotados como @RequestAttribute
                    .requestAttr(USER_ID_ATTRIBUTE_NAME, jwtData.getUserID())
                    .requestAttr(SERVICE_TOKEN_ATTRIBUTE_NAME, jwtData.toString())
                    .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN_PREFIX + authUserDTO.getServiceToken())
                    .header(HttpHeaders.ACCEPT_LANGUAGE, locale.getLanguage())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(encodedBodyContent)
        );

        // Comprobar resultados
        actions
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(expectedContent));
    }

    @Test
    void whenUpdateCustomizedCategory_thenCustomizedCategoryDTO() throws Exception {
        // Crear datos de prueba
        User validUser = testUtils.generateValidUser();
        AuthenticatedUserDTO authUserDTO = testUtils.generateAuthenticatedUser(validUser);      // Registra un usuario y obtiene el DTO respuesta
        JwtData jwtData = jwtGenerator.extractInfoFromToken(authUserDTO.getServiceToken());
        Category validCategory = testUtils.registerValidCategory();
        CustomizedCategory customizedCategory = testUtils.registerCustomizedCategory(validUser, validCategory);

        GenericValueDTO<Float> paramsDTO = new GenericValueDTO<>(90F);


        // Ejecutar funcionalidades
        String endpoint = API_ENDPOINT + "/categories/" + validCategory.getId().toString();
        String encodedBodyContent = this.jsonMapper.writeValueAsString(paramsDTO);
        ResultActions actions = mockMvc.perform(
                put(endpoint)
                        // Valores anotados como @RequestAttribute
                        .requestAttr(USER_ID_ATTRIBUTE_NAME, jwtData.getUserID())
                        .requestAttr(SERVICE_TOKEN_ATTRIBUTE_NAME, jwtData.toString())
                        .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN_PREFIX + authUserDTO.getServiceToken())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, locale.getLanguage())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(encodedBodyContent)
        );

        // Comprobar resultados
        CustomizedCategoryDTO expectedResponse = CategoryConversor.toCustomizedCategoryDTO(customizedCategory);
        String expectedResponseBody = this.jsonMapper.writeValueAsString(expectedResponse);
        actions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedResponseBody));
    }

    @Test
    void whenGetCustomizedCategoriesByUser_thenCustomizedCategoryDTOList() throws Exception {
        // Crear datos de prueba
        User validUser = testUtils.generateValidUser();
        AuthenticatedUserDTO authUserDTO = testUtils.generateAuthenticatedUser(validUser);      // Registra un usuario y obtiene el DTO respuesta
        JwtData jwtData = jwtGenerator.extractInfoFromToken(authUserDTO.getServiceToken());
        Category validCategory1 = testUtils.registerValidCategory(testUtils.DEFAULT_CATEGORY_NAME);
        Category validCategory2 = testUtils.registerValidCategory(testUtils.DEFAULT_CATEGORY_NAME + 2);
        testUtils.registerCustomizedCategory(validUser, validCategory1);
        testUtils.registerCustomizedCategory(validUser, validCategory2);

        // Ejecutar funcionalidades
        String endpoint = API_ENDPOINT + "/categories/" + validUser.getId();

        ResultActions actions = mockMvc.perform(
                get(endpoint)
                        // Valores anotados como @RequestAttribute
                        .requestAttr(USER_ID_ATTRIBUTE_NAME, jwtData.getUserID())
                        .requestAttr(SERVICE_TOKEN_ATTRIBUTE_NAME, jwtData.toString())
                        .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN_PREFIX + authUserDTO.getServiceToken())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, locale.getLanguage())
        );

        // Comprobar resultados
        List<CustomizedCategory> serviceResponse = ticketService.getCustomCategoriesByUser(validUser.getId());
        List<CustomizedCategoryDTO> expectedResponseContent = CategoryConversor.toCustomizedCategoryDTOList(serviceResponse);
        String expectedResponseBody = this.jsonMapper.writeValueAsString(expectedResponseContent);
        actions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedResponseBody));
    }


    @Test
    void whenCreateTicket_thenTicketDTO() throws Exception {
        // Crear datos de prueba
        User author = testUtils.generateValidUser();
        AuthenticatedUserDTO authorAuthDTO = testUtils.generateAuthenticatedUser(author);
        JwtData authorJwtData = jwtGenerator.extractInfoFromToken(authorAuthDTO.getServiceToken());
        Category validCategory = testUtils.registerValidCategory();
        CustomizedCategory customizedCategory = testUtils.registerCustomizedCategory(author, validCategory);
        ParsedTicketData parsedTicketData = testUtils.registerParsedTicketData();

        CreateTicketParamsDTO paramsDTO = testUtils.generateCreateTicketParamsDTO(author, customizedCategory, parsedTicketData);

        // Ejecutar funcionalidades
        String endpoint = API_ENDPOINT + "/";
        String encodedBodyContent = this.jsonMapper.writeValueAsString(paramsDTO);
        ResultActions actions = mockMvc.perform(
                post(endpoint)
                        // Valores anotados como @RequestAttribute
                        .requestAttr(USER_ID_ATTRIBUTE_NAME, authorJwtData.getUserID())
                        .requestAttr(SERVICE_TOKEN_ATTRIBUTE_NAME, authorJwtData.toString())
                        .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN_PREFIX + authorAuthDTO.getServiceToken())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, locale.getLanguage())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(encodedBodyContent)
        );
        Ticket expectedResponse = author.getTickets().stream().findFirst().get();
        String encodedResponseBodyContent = this.jsonMapper.writeValueAsString(TicketConversor.toTicketDTO(expectedResponse));

        // Comprobar resultados
        actions
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(encodedResponseBodyContent));
    }

    @Test
    void whenShareTicket_thenTicketDTO() throws Exception {
        // Crear datos de prueba
        User author = testUtils.generateValidUser("author");
        userRepository.save(author);
        User receiver = testUtils.generateValidUser("receiver");
        userRepository.save(receiver);
        Category validCategory = testUtils.registerValidCategory();
        CustomizedCategory customizedCategory = testUtils.registerCustomizedCategory(author, validCategory);
        ParsedTicketData parsedTicketData = testUtils.registerParsedTicketData();
        Ticket ticket = testUtils.registerTicket(customizedCategory, author, parsedTicketData);
        AuthenticatedUserDTO authorAuthDTO = testUtils.generateAuthenticatedUser(author);
        JwtData authorJwtData = jwtGenerator.extractInfoFromToken(authorAuthDTO.getServiceToken());

        ShareTicketParamsDTO paramsDTO = new ShareTicketParamsDTO(author.getId(), receiver.getNickname());

        // Ejecutar funcionalidades
        String endpoint = API_ENDPOINT + "/share/" + ticket.getId().toString();
        String encodedBodyContent = this.jsonMapper.writeValueAsString(paramsDTO);
        ResultActions actions = mockMvc.perform(
                post(endpoint)
                        // Valores anotados como @RequestAttribute
                        .requestAttr(USER_ID_ATTRIBUTE_NAME, authorJwtData.getUserID())
                        .requestAttr(SERVICE_TOKEN_ATTRIBUTE_NAME, authorJwtData.toString())
                        .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN_PREFIX + authorAuthDTO.getServiceToken())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, locale.getLanguage())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(encodedBodyContent)
        );
        Ticket expectedResponse = ticketRepository.findById(ticket.getId()).get();
        String encodedResponseBodyContent = this.jsonMapper.writeValueAsString(TicketConversor.toTicketDTO(expectedResponse));

        // Comprobar resultados
        actions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(encodedResponseBodyContent));
    }

    @Test
    void whenDeleteTicket_thenNoContent() throws Exception {
        // Crear datos de prueba
        User author = testUtils.generateValidUser();
        AuthenticatedUserDTO authorAuthDTO = testUtils.generateAuthenticatedUser(author);
        JwtData authorJwtData = jwtGenerator.extractInfoFromToken(authorAuthDTO.getServiceToken());
        Category validCategory = testUtils.registerValidCategory();
        CustomizedCategory customizedCategory = testUtils.registerCustomizedCategory(author, validCategory);
        ParsedTicketData parsedTicketData = testUtils.registerParsedTicketData();
        Ticket existentTicket = testUtils.registerTicket(customizedCategory, author, parsedTicketData);

        // Ejecutar funcionalidades
        String endpoint = API_ENDPOINT + "/" + existentTicket.getId();
        ResultActions actions = mockMvc.perform(
                delete(endpoint)
                        // Valores anotados como @RequestAttribute
                        .requestAttr(USER_ID_ATTRIBUTE_NAME, authorJwtData.getUserID())
                        .requestAttr(SERVICE_TOKEN_ATTRIBUTE_NAME, authorJwtData.toString())
                        .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN_PREFIX + authorAuthDTO.getServiceToken())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, locale.getLanguage())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // Comprobar resultados
        actions
                .andExpect(status().isNoContent());
    }

    @Test
    void whenGetTicketDetails_thenTicketDTO() throws Exception {
        // Crear datos de prueba
        User author = testUtils.generateValidUser();
        AuthenticatedUserDTO authorAuthDTO = testUtils.generateAuthenticatedUser(author);
        JwtData authorJwtData = jwtGenerator.extractInfoFromToken(authorAuthDTO.getServiceToken());
        Category validCategory = testUtils.registerValidCategory();
        CustomizedCategory customizedCategory = testUtils.registerCustomizedCategory(author, validCategory);
        ParsedTicketData parsedTicketData = testUtils.registerParsedTicketData();
        Ticket ticket = testUtils.registerTicket(customizedCategory, author, parsedTicketData);

        // Ejecutar funcionalidades
        String endpoint = API_ENDPOINT + "/" + ticket.getId();
        ResultActions actions = mockMvc.perform(
                get(endpoint)
                        // Valores anotados como @RequestAttribute
                        .requestAttr(USER_ID_ATTRIBUTE_NAME, authorJwtData.getUserID())
                        .requestAttr(SERVICE_TOKEN_ATTRIBUTE_NAME, authorJwtData.toString())
                        .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN_PREFIX + authorAuthDTO.getServiceToken())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, locale.getLanguage())
                        .contentType(MediaType.APPLICATION_JSON)
        );
        String encodedResponseBodyContent = this.jsonMapper.writeValueAsString(TicketConversor.toTicketDTO(ticket));

        // Comprobar resultados
        actions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(encodedResponseBodyContent));
    }

    @Test
    void whenfilterUserTicketsByCriteria_thenTicketDTOList() throws Exception {
        // Crear datos de prueba
        User author = testUtils.generateValidUser();
        AuthenticatedUserDTO authorAuthDTO = testUtils.generateAuthenticatedUser(author);
        JwtData authorJwtData = jwtGenerator.extractInfoFromToken(authorAuthDTO.getServiceToken());
        Category validCategory = testUtils.registerValidCategory();
        CustomizedCategory customizedCategory = testUtils.registerCustomizedCategory(author, validCategory);
        ParsedTicketData parsedTicketData = testUtils.registerParsedTicketData();
        Ticket ticket = testUtils.registerTicket(customizedCategory, author, parsedTicketData);

        // Ejecutar funcionalidades
        String endpoint = API_ENDPOINT + "/" + author.getId();
        TicketFilterParamsDTO paramsDTO = testUtils.generateSuccessfulTicketFilterParamsDTOFromTicket(ticket);
        String encodedBodyContent = this.jsonMapper.writeValueAsString(paramsDTO);
        ResultActions actions = mockMvc.perform(
                put(endpoint)
                        // Valores anotados como @RequestAttribute
                        .requestAttr(USER_ID_ATTRIBUTE_NAME, authorJwtData.getUserID())
                        .requestAttr(SERVICE_TOKEN_ATTRIBUTE_NAME, authorJwtData.toString())
                        .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN_PREFIX + authorAuthDTO.getServiceToken())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, locale.getLanguage())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(encodedBodyContent)
        );
        List<Ticket> expectedResponse = ticketService.filterUserTicketsByCriteria(author.getId(), paramsDTO);
        String encodedResponseBodyContent = this.jsonMapper.writeValueAsString(TicketConversor.toTicketDTOList(expectedResponse));

        // Comprobar resultados
        actions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(encodedResponseBodyContent));
    }
    @Test
    void getSharedTickets() throws Exception {
        // Crear datos de prueba
        User author = testUtils.generateValidUser("author");
        userRepository.save(author);
        User receiver = testUtils.generateValidUser("receiver");
        Category validCategory = testUtils.registerValidCategory();
        CustomizedCategory customizedCategory = testUtils.registerCustomizedCategory(author, validCategory);
        ParsedTicketData parsedTicketData = testUtils.registerParsedTicketData();
        Ticket ticket = testUtils.registerTicket(customizedCategory, author, parsedTicketData);
        Set<Ticket> ticketSet = new HashSet<>();
        ticketSet.add(ticket);

        receiver.setTickets(ticketSet);
        userRepository.save(receiver);

        AuthenticatedUserDTO receiverAuthDTO = testUtils.generateAuthenticatedUser(receiver);
        JwtData receiverJwtData = jwtGenerator.extractInfoFromToken(receiverAuthDTO.getServiceToken());

        String endpoint = API_ENDPOINT + "/sharedTickets";

        ResultActions actions = mockMvc.perform(
                get(endpoint)
                        // Valores anotados como @RequestAttribute
                        .requestAttr(USER_ID_ATTRIBUTE_NAME, receiverJwtData.getUserID())
                        .requestAttr(SERVICE_TOKEN_ATTRIBUTE_NAME, receiverJwtData.toString())
                        .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN_PREFIX + receiverAuthDTO.getServiceToken())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, locale.getLanguage())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        List<Ticket> expectedResponse = userRepository.getSharedTickets(receiver.getId());
        String encodedResponseBodyContent = this.jsonMapper.writeValueAsString(TicketConversor.toTicketDTOList(expectedResponse));

        // Comprobar resultados
        actions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(encodedResponseBodyContent));
    }

}
