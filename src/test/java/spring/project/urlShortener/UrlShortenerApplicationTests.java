package spring.project.urlShortener;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import spring.project.urlShortener.config.CreateUrlHandler;
import spring.project.urlShortener.config.StringGenerator;
import spring.project.urlShortener.config.URLValidator;
import spring.project.urlShortener.exceptions.BadRequestException;
import spring.project.urlShortener.exceptions.ResourceNotFoundException;
import spring.project.urlShortener.models.dtos.ResponseDto;
import spring.project.urlShortener.models.dtos.CustomUrlRequest;
import spring.project.urlShortener.models.entities.Url;
import spring.project.urlShortener.repository.UrlRepository;
import spring.project.urlShortener.services.UrlService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class UrlShortenerApplicationTests
{

	@Mock
	private UrlRepository urlRepository;

	@Mock
	private StringGenerator stringGenerator;

	@Mock
	private URLValidator urlValidator;

	@InjectMocks
	private CreateUrlHandler createUrlHandler;

	@InjectMocks
	private UrlService urlService;

    @Nested

    @DisplayName("CreateUrl Method")
	class createUrlHandler{
		@Test
		@DisplayName("Create a new url successfully with a random string")
		void createRandomUrl_successfully() {
			CustomUrlRequest customUrlRequest = new CustomUrlRequest();
			customUrlRequest.setLongUrl("https://google.com");

			when(urlRepository.findByLongUrlAndIsDeletedIsFalse("https://google.com")).thenReturn(Optional.empty());
			when(stringGenerator.generateString()).thenReturn("ShortUrl");
			when(urlRepository.existsUrlByShortenedUrlStringAndIsDeletedIsFalse("ShortUrl")).thenReturn(false);
			when(urlValidator.isValidUrl("https://google.com")).thenReturn(true);

			when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
				Url savedUrl = invocation.getArgument(0);
				savedUrl.setId(1L);
				return savedUrl;
			});

			ResponseDto<Url> response = createUrlHandler.createUrlHandler(customUrlRequest);

			assertNotNull(response);
			assertEquals("https://google.com", response.getResponse().getLongUrl());
			assertEquals("ShortUrl with id 1 created", response.getMessage());
			verify(urlRepository).save(any(Url.class));
		}

		@Test
		@DisplayName("Create a new url successfully with a custom string")
		void createCustomUrl_successfully() {
			CustomUrlRequest customUrlRequest = new CustomUrlRequest();
			customUrlRequest.setLongUrl("https://google.com");
			customUrlRequest.setCustomUrlString("customUrl");

			when(urlValidator.isValidUrl(customUrlRequest.getLongUrl())).thenReturn(true);
			when(urlRepository.findByLongUrlAndIsDeletedIsFalse(customUrlRequest.getLongUrl()))
					.thenReturn(Optional.empty());
			when(urlRepository.findByLongUrlAndIsDeletedIsTrue(customUrlRequest.getLongUrl()))
					.thenReturn(Optional.empty());
			when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
				Url savedUrl = invocation.getArgument(0);
				savedUrl.setId(1L);
				return savedUrl;
			});

			ResponseDto<Url> response = createUrlHandler.createUrlHandler(customUrlRequest);

			assertNotNull(response);
			assertEquals("https://google.com", response.getResponse().getLongUrl());
			assertEquals("ShortUrl with id 1 created", response.getMessage());
			verify(urlRepository).save(any(Url.class));
		}

		@Test
		@DisplayName("Create a new url, invalid url (bad request)")
		void createUrlHandler_invalidUrl() {
			CustomUrlRequest customUrlRequest = new CustomUrlRequest();
			customUrlRequest.setLongUrl("https://example.com");
			when(urlValidator.isValidUrl(customUrlRequest.getLongUrl())).thenReturn(false);

			assertThrows(BadRequestException.class, () -> createUrlHandler.createUrlHandler(customUrlRequest));
			verify(urlValidator).isValidUrl(customUrlRequest.getLongUrl());
		}

		@Test
		@DisplayName("Create a new url, invalid url (url not found)")
		void createUrl_urlAlreadyExists() {
			CustomUrlRequest customUrlRequest = new CustomUrlRequest();
			customUrlRequest.setLongUrl("https://google.com");

			Url url = new Url();
			customUrlRequest.setLongUrl(customUrlRequest.getLongUrl());
			url.setId(1L);

			when(urlValidator.isValidUrl("https://google.com")).thenReturn(true);
			when(urlRepository.findByLongUrlAndIsDeletedIsTrue("https://google.com")).thenReturn(Optional.of(url));

			ResponseDto<Url> response = createUrlHandler.createUrlHandler(customUrlRequest);

			assertEquals("ShortUrl with id 1 created", response.getMessage() );
		}

		@Test
		void createUrl_whenUrlExistsAndDeleted_reactivateUrl() {
			String longUrl = "https://example.com";
			CustomUrlRequest customUrlRequest = new CustomUrlRequest();
			customUrlRequest.setLongUrl(longUrl);

			Url deletedUrl = new Url();
			deletedUrl.setId(1L);
			deletedUrl.setLongUrl(longUrl);
			deletedUrl.setIsDeleted(true);

			when(urlValidator.isValidUrl(longUrl)).thenReturn(true);
			when(urlRepository.findByLongUrlAndIsDeletedIsFalse(longUrl))
					.thenReturn(Optional.empty());
			when(urlRepository.findByLongUrlAndIsDeletedIsTrue(longUrl))
					.thenReturn(Optional.of(deletedUrl));

			ResponseDto<Url> result = createUrlHandler.createUrlHandler(customUrlRequest);

			assertNotNull(result);
			assertFalse(result.getResponse().getIsDeleted());
			assertEquals("ShortUrl with id 1 created", result.getMessage());
			verify(urlRepository).save(deletedUrl);
		}

	}

	@Nested
	@DisplayName("redirect method")
	class redirect{
		@Test
		@DisplayName("Redirect to page for longUrl successfully")
		void redirect_successfully() {
			String shortUrlString = "idea-age";

			Url longUrl = new Url();
			longUrl.setLongUrl("https://google.com");
			longUrl.setCreatedAt(LocalDateTime.now());
			longUrl.setExpiresAt(LocalDateTime.now().plusDays(2));

			when(urlRepository.findByShortenedUrlStringAndIsDeletedIsFalse(shortUrlString)).thenReturn(Optional.of(longUrl));
			when(urlValidator.isValidUrl("https://google.com")).thenReturn(true);

			ResponseDto<Url> redirect = urlService.redirect(shortUrlString);

			assertEquals("Redirecting to long url", redirect.getMessage());
			assertNotNull(redirect.getResponse());
			assertEquals(longUrl, redirect.getResponse());

		}

		@Test
		@DisplayName("Redirect to page for longUrl, by returning URL details, url not found")
		void redirect_urlExpired() {
			String shortUrlString = "idea-age";

			Url expiredUrl = new Url();
			expiredUrl.setLongUrl("https://google.com");
			expiredUrl.setCreatedAt(LocalDateTime.now());
			expiredUrl.setExpiresAt(LocalDateTime.now().minusDays(2));
			expiredUrl.setIsExpired(false);

			when(urlRepository.findByShortenedUrlStringAndIsDeletedIsFalse(shortUrlString)).thenReturn(Optional.of(expiredUrl));

			ResponseDto<Url> redirect = urlService.redirect(shortUrlString);

			assertEquals("URL not found, it's invalid or expired", redirect.getMessage());
		}

		@Test
		@DisplayName("redirect short url, invalid url (url not found)")
		void redirect_invalidUrl() {
			String shortUrlString = "idea-age";

			Url longUrl = new Url();
			longUrl.setLongUrl("https://google.com");
			longUrl.setCreatedAt(LocalDateTime.now());
			longUrl.setExpiresAt(LocalDateTime.now().plusDays(2));

			when(urlRepository.findByShortenedUrlStringAndIsDeletedIsFalse(shortUrlString)).thenReturn(Optional.of(longUrl));
			when(urlValidator.isValidUrl("https://google.com")).thenReturn(false);

			ResponseDto<Url> redirect = urlService.redirect(shortUrlString);

			assertEquals("URL not found, it's invalid or expired", redirect.getMessage());
		}
	}

	@Nested
	@DisplayName("Get URL by ID")
	class getUrl{
		@Test
		@DisplayName("Returns URL by id successfully")
		void getUrl_successfully() {
		Url longUrl = new Url();
			longUrl.setLongUrl("https://google.com");
			longUrl.setId(1L);

			when(urlRepository.findByIdAndIsDeletedIsFalse(longUrl.getId())).thenReturn(Optional.of(longUrl));
			ResponseDto<Url> result = urlService.getUrl(longUrl.getId());

			assertEquals(longUrl, result.getResponse());
			assertEquals("Returning long url by ID", result.getMessage());
		}

		@Test
		@DisplayName("URL with id doesn't exist")
		void getUrl_doesNotExist() {
			Url longUrl = new Url();

			when(urlRepository.findByIdAndIsDeletedIsFalse(longUrl.getId())).thenReturn(Optional.empty());

			assertThrows(ResourceNotFoundException.class, () -> {urlService.getUrl(longUrl.getId());});
		}
	}

	@Nested
	@DisplayName("Update url")
	class updateUrl{
		@Test
		@DisplayName("update url object details successfully")
		void updateUrl_successfully() {
			CustomUrlRequest customUrlRequest = new CustomUrlRequest();
			customUrlRequest.setCustomUrlString("ggl-ml");

			Url longUrl = new Url();
			longUrl.setLongUrl("https://google.com");
			longUrl.setId(1L);

			when(urlRepository.findByIdAndIsDeletedIsFalse(longUrl.getId())).thenReturn(Optional.of(longUrl));
			longUrl.setShortenedUrlString(customUrlRequest.getCustomUrlString());
			when(urlRepository.save(longUrl)).thenReturn(longUrl);

			ResponseDto<String> result = urlService.updateUrl(longUrl.getId(), customUrlRequest);

			assertEquals("Successfully updated url with id 1" , result.getMessage());
			assertEquals("ggl-ml", longUrl.getShortenedUrlString());
		}

		@Test
		@DisplayName("Url Object doesn't exist")
		void updateUrl_doesNotExist() {
			CustomUrlRequest customUrlRequest = new CustomUrlRequest();

			assertThrows(ResourceNotFoundException.class, () -> {urlService.updateUrl(1L, customUrlRequest);});
		}
	}

	@Nested
	@DisplayName("Delete url")
	class deleteUrl{
		@Test
		@DisplayName("Delete url object successfully")
		void deleteUrl_successfully() {
			Url longUrl = new Url();
			longUrl.setLongUrl("https://google.com");
			longUrl.setId(1L);

			when(urlRepository.findByIdAndIsDeletedIsFalse(longUrl.getId())).thenReturn(Optional.of(longUrl));
			longUrl.setIsDeleted(true);
			when(urlRepository.save(longUrl)).thenReturn(longUrl);

			ResponseDto<String> result = urlService.deleteUrl(longUrl.getId());

			assertEquals("Successfully deleted url with id 1", result.getMessage());
		}

		@Test
		@DisplayName("Url Object doesn't exist")
		void deleteUrl_doesNotExist() {
			Url longUrl = new Url();

			when(urlRepository.findByIdAndIsDeletedIsFalse(longUrl.getId())).thenReturn(Optional.empty());

			assertThrows(ResourceNotFoundException.class, () -> {urlService.deleteUrl(longUrl.getId());});
		}
	}
}