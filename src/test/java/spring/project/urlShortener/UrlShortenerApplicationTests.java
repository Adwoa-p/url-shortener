package spring.project.urlShortener;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.view.RedirectView;
import spring.project.urlShortener.config.StringGenerator;
import spring.project.urlShortener.models.dtos.ResponseDto;
import spring.project.urlShortener.models.dtos.UrlDto;
import spring.project.urlShortener.models.entities.Url;
import spring.project.urlShortener.repository.UrlRepository;
import spring.project.urlShortener.services.UrlService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class UrlShortenerApplicationTests
{

	@Mock
	private UrlRepository urlRepository;

	@Mock
	private StringGenerator stringGenerator;

	@InjectMocks
	private UrlService urlService;

    @Nested

    @DisplayName("CreateUrl Method")
	class createUrl{
		@Test
		@DisplayName("Create a new url successfully")
		void createUrl_successfully() {
			UrlDto urlDto = new UrlDto();
			urlDto.setLongUrl("https://google.com");

			when(stringGenerator.generateString()).thenReturn("ShortUrl");

			when(urlRepository.existsUrlByShortenedUrlString("ShortUrl")).thenReturn(false);

			when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
				Url savedUrl = invocation.getArgument(0);
				savedUrl.setId(1L);
				return savedUrl;
			});

			ResponseDto<Url> response = urlService.createUrl(urlDto);

			assertNotNull(response);
			assertEquals("https://google.com", response.getResponse().getLongUrl());
			assertEquals("ShortUrl with id 1 created", response.getMessage());
			assertEquals(8, response.getResponse().getShortenedUrlString().length());
			verify(urlRepository).save(any(Url.class));
		}

		@Test
		@DisplayName("Create a new url, string already exists")
		void createUrl_urlStringAlreadyExists() {
			UrlDto urlDto = new UrlDto();
			urlDto.setLongUrl("https://example.com");

			when(stringGenerator.generateString()).thenReturn("ShortUrl");
			when(urlRepository.existsUrlByShortenedUrlString("ShortUrl")).thenReturn(true);

			ResponseDto<Url> response = urlService.createUrl(urlDto);

			assertNotNull(response);
			assertEquals("ShortUrl already exists. Try again with another", response.getMessage());
			assertNull(response.getResponse());
		}
	}

	@Nested
	@DisplayName("createurl using a custom string")
	class createCustomUrl{
		@Test
		@DisplayName("Create a new url using a custom string successfully")
		void createCustomUrl_successfully() {
			UrlDto urlDto = new UrlDto();
			urlDto.setLongUrl("https://google.com");
			urlDto.setCustomUrlString("idea-age");

			when(urlRepository.existsUrlByShortenedUrlString("idea-age")).thenReturn(false);

			when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
				Url savedUrl = invocation.getArgument(0);
				savedUrl.setId(1L);
				return savedUrl;
			});

			ResponseDto<Url> response = urlService.createCustomUrl(urlDto);

			assertNotNull(response);
			assertEquals("https://google.com", response.getResponse().getLongUrl());
			assertEquals("ShortUrl with id 1 created", response.getMessage());
			assertEquals(8, response.getResponse().getShortenedUrlString().length());
			verify(urlRepository).save(any(Url.class));
		}

		@Test
		@DisplayName("Create a new url using a custom string, string already exists")
		void createCustomUrl_customUrlStringAlreadyExists() {
			UrlDto urlDto = new UrlDto();
			urlDto.setLongUrl("https://example.com");
			urlDto.setCustomUrlString("idea-age");

			when(urlRepository.existsUrlByShortenedUrlString("idea-age")).thenReturn(true);

			ResponseDto<Url> response = urlService.createCustomUrl(urlDto);

			assertNotNull(response);
			assertEquals("ShortUrl already exists. Try again with another", response.getMessage());
			assertNull(response.getResponse());

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

			when(urlRepository.findByShortenedUrlString(shortUrlString)).thenReturn(longUrl);

			RedirectView actualRedirectView = urlService.getUrl(shortUrlString);

			assertEquals(longUrl.getLongUrl(), actualRedirectView.getUrl());

		}

		@Test
		@DisplayName("Redirect to page for longUrl, url not found")
		void redirect_urlExpired_urlNotFound() {
			String shortUrlString = "idea-age";

			Url expiredUrl = new Url();
			expiredUrl.setLongUrl("https://google.com");
			expiredUrl.setCreatedAt(LocalDateTime.now());
			expiredUrl.setExpiresAt(LocalDateTime.now().minusDays(2));
			expiredUrl.setIsExpired(false);

			when(urlRepository.findByShortenedUrlString(shortUrlString)).thenReturn(expiredUrl);

			RedirectView redirectView = urlService.getUrl(shortUrlString);

			assertEquals("/error", redirectView.getUrl());
			assertTrue(expiredUrl.getIsExpired());
		}
	}
}