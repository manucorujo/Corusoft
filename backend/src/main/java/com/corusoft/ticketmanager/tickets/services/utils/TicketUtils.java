package com.corusoft.ticketmanager.tickets.services.utils;

import com.corusoft.ticketmanager.common.exceptions.EntityNotFoundException;
import com.corusoft.ticketmanager.common.exceptions.UnableToParseImageException;
import com.corusoft.ticketmanager.tickets.entities.*;
import com.corusoft.ticketmanager.tickets.repositories.CategoryRepository;
import com.corusoft.ticketmanager.tickets.repositories.CustomizedCategoryRepository;
import com.corusoft.ticketmanager.tickets.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Path;
import java.util.Base64;

import static com.corusoft.ticketmanager.TicketManagerApplication.TEMP_PATH;

@Component
@Transactional(readOnly = true)
public class TicketUtils {

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private CustomizedCategoryRepository customizedCategoryRepo;

    @Autowired
    private TicketRepository ticketRepo;

    /**
     * Busca una categoría por su nombre en la base de datos.
     *
     * @param name Nombre de la categoría a buscar
     * @return Categoría encontrada
     * @throws EntityNotFoundException No se encuentra al usuario
     */
    public Category fetchCategoryByName(String name) throws EntityNotFoundException {
        return categoryRepo.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException(Category.class.getSimpleName(), name));
    }

    /**
     * Busca una categoría por su id en la base de datos.
     *
     * @param categoryId de la categoría a buscar
     * @return Categoría encontrada
     * @throws EntityNotFoundException
     */
    public Category fetchCategoryById(Long categoryId) throws EntityNotFoundException {
        return categoryRepo.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(Category.class.getSimpleName(), categoryId));
    }


    /**
     * Busca una categoría customizada en la base de datos.
     *
     * @param customizedCategoryID de la categoría customizada a buscar.
     * @return Categoría Customizada encontrada
     * @throws EntityNotFoundException No se encuentra la categoría customizada asociada al usuario.
     */
    public CustomizedCategory fetchCustomizedCategoryById(CustomizedCategoryID customizedCategoryID)
            throws EntityNotFoundException {
        return customizedCategoryRepo.findById(customizedCategoryID).orElseThrow(() ->
                new EntityNotFoundException(CustomizedCategory.class.getSimpleName(), customizedCategoryID));

    }

    /**
     * Crea un <c>File</c> a partir de la imágen recibida, codificada como un String en Base64
     * @param imageAsB64String - Imágen codificada en Base64
     * @return File que representa la imágen
     */
    public File parseB64ImageToFile(String imageAsB64String) throws UnableToParseImageException {
        String imageB64DataString = "";
        String imageFileExtension = ".jpg";

        // Separa la cabecera "data:image/{tipo_imagen}" de la imagen en Base 64 si la tiene
        if (imageAsB64String.contains(",")) {
            String[] splittedB64ImageHeaders = imageAsB64String.split(",");
            // Obtiene extensión de la imágen: jpg, jpeg, png, ...
            String extension = splittedB64ImageHeaders[0]
                    .split("/")[1]
                    .split(";")[0];
            if (!extension.equals("*")) {
                imageFileExtension = "." + extension;
            }

            // Datos de la imágen
            imageB64DataString = splittedB64ImageHeaders[1];
        }

        // Decodificar imagen recibida
        byte[] pictureBytes;
        try {
            pictureBytes = Base64.getDecoder().decode(imageB64DataString);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            throw new UnableToParseImageException();
        }

        // Crear fichero con los datos de la imágen
        File imageFile;
        try {
            Path tempDirectoryPath = TEMP_PATH;
            imageFile = File.createTempFile("image-", imageFileExtension, new File(TEMP_PATH.toString()));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new UnableToParseImageException();
        }

        // Escribir datos de imagen a fichero
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            fos.write(pictureBytes);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new UnableToParseImageException();
        }

        return imageFile;
    }

    public byte[] parseImage64StringToBytes(String imageAsB64String) throws UnableToParseImageException {
        byte[] imageBytes;
        String imageB64DataString;

        imageB64DataString = imageAsB64String.contains(",") ?
                imageAsB64String.split(",")[1]
                : imageAsB64String;

        try {
            imageBytes = Base64.getDecoder().decode(imageB64DataString);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            throw new UnableToParseImageException();
        }

        return imageBytes;
    }

    /**
     * Busca un Ticket en la base de datos.
     *
     * @param ticketID del ticket a buscar.
     * @return Ticket encontrado.
     * @throws EntityNotFoundException si no se encuentra el ticket.
     */
    public Ticket fetchTicketById(Long ticketID) throws EntityNotFoundException {
        return ticketRepo.findById(ticketID).orElseThrow(
                () -> new EntityNotFoundException(Ticket.class.getSimpleName(), ticketID));
    }
}
