package options.papertrading.services;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.models.option.Option;
import options.papertrading.repositories.OptionsRepository;
import options.papertrading.util.converters.OptionConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OptionsServiceTest {

    @Mock
    private OptionsRepository optionsRepository;

    @Mock
    private BeanFactory beanFactory;

    @Mock
    private OptionConverter optionConverter;

    @InjectMocks
    private OptionsService optionsService;

    @Test
    void findAll_ShouldReturnAllOptions() {
        Option mockOption = mock(Option.class);
        when(optionsRepository.findAll()).thenReturn(Collections.singletonList(mockOption));

        List<Option> result = optionsService.findAll();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(mockOption, result.get(0)),
                () -> verify(optionsRepository).findAll()
        );
    }

    @Test
    void showOptionsListByPrefix_ShouldReturnFilteredOptions() {
        Option mockOption = mock(Option.class);
        OptionDto mockDto = mock(OptionDto.class);

        when(mockOption.getDaysToMaturity()).thenReturn(1);
        when(optionsRepository.findByIdStartingWith("TEST")).thenReturn(Collections.singletonList(mockOption));
        when(optionConverter.convertToOptionDto(mockOption)).thenReturn(mockDto);

        List<OptionDto> result = optionsService.showOptionsListByPrefix("TEST");

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(mockDto, result.get(0)),
                () -> verify(optionsRepository).findByIdStartingWith("TEST")
        );
    }

    @Test
    void showOptionsFromCurrentPortfolios_ShouldConvertPortfolios() {
        PortfolioDto mockPortfolio = mock(PortfolioDto.class);
        Option mockOption = mock(Option.class);
        OptionDto mockDto = mock(OptionDto.class);

        when(mockPortfolio.getId()).thenReturn("OPT1");
        when(optionsRepository.findOneById("OPT1")).thenReturn(mockOption);
        when(optionConverter.convertToOptionDto(mockOption)).thenReturn(mockDto);
        when(beanFactory.getBean(OptionsService.class)).thenReturn(optionsService);

        List<OptionDto> result = optionsService.showOptionsFromCurrentPortfolios(
                Collections.singletonList(mockPortfolio));

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(mockDto, result.get(0)),
                () -> verify(optionsRepository).findOneById("OPT1")
        );
    }

    @Test
    void findByOptionId_ShouldReturnOption() {
        Option mockOption = mock(Option.class);
        when(optionsRepository.findOneById("OPT1")).thenReturn(mockOption);

        Option result = optionsService.findByOptionId("OPT1");

        assertAll(
                () -> assertEquals(mockOption, result),
                () -> verify(optionsRepository).findOneById("OPT1")
        );
    }

    @Test
    void showOptionsList_ShouldReturnConvertedOptions() {
        Option mockOption = mock(Option.class);
        OptionDto mockDto = mock(OptionDto.class);

        when(optionsRepository.findAll()).thenReturn(Collections.singletonList(mockOption));
        when(optionConverter.convertListToOptionDtoList(anyList())).thenReturn(Collections.singletonList(mockDto));
        when(beanFactory.getBean(OptionsService.class)).thenReturn(optionsService);

        List<OptionDto> result = optionsService.showOptionsList();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(mockDto, result.get(0)),
                () -> verify(optionsRepository).findAll()
        );
    }

    @Test
    void showOptionsListByPrefix_ShouldBeCacheable() throws NoSuchMethodException {
        Method method = OptionsService.class.getMethod("showOptionsListByPrefix", String.class);
        Cacheable cacheable = AnnotationUtils.findAnnotation(method, Cacheable.class);

        assertAll(
                () -> assertNotNull(cacheable),
                () -> assertEquals("OptionsService::showOptionsListByPrefix", cacheable.value()[0]),
                () -> assertEquals("#prefix", cacheable.key())
        );
    }

    @Test
    void findAll_ShouldBeTransactionalReadOnly() throws NoSuchMethodException {
        Method method = OptionsService.class.getMethod("findAll");
        Transactional transactional = AnnotationUtils.findAnnotation(method, Transactional.class);

        assertAll(
                () -> assertNotNull(transactional),
                () -> assertTrue(transactional.readOnly())
        );
    }

    @Test
    void findByOptionId_ShouldThrowException_WhenIdIsNull() {
        assertThrows(NullPointerException.class, () -> optionsService.findByOptionId(null));
    }
}