package options.papertrading.services;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.models.option.Option;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.OptionsRepository;
import options.papertrading.repositories.PersonsRepository;
import options.papertrading.repositories.PortfoliosRepository;
import options.papertrading.util.converters.TextConverter;
import options.papertrading.util.mail.SmtpMailSender;
import options.papertrading.util.mappers.OptionMapper;
import options.papertrading.util.mappers.PersonMapper;
import options.papertrading.util.validators.VolumeValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

class OptionsServiceDiffblueTest {
    /**
     * Method under test: {@link OptionsService#findAll()}
     */
    @Test
    void testFindAll() {
        // Arrange
        OptionsRepository optionsRepository = mock(OptionsRepository.class);
        ArrayList<Option> optionList = new ArrayList<>();
        when(optionsRepository.findAll()).thenReturn(optionList);
        OptionMapper optionMapper = mock(OptionMapper.class);
        VolumeValidator volumeValidator = new VolumeValidator();
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
        OptionsService optionsService = new OptionsService(optionsRepository, optionMapper, volumeValidator,
                new PositionSetter(null, new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                        passwordEncoder, new TextConverter()), mock(PortfoliosRepository.class)));

        // Act
        List<Option> actualFindAllResult = optionsService.findAll();

        // Assert
        verify(optionsRepository).findAll();
        assertTrue(actualFindAllResult.isEmpty());
        assertEquals(actualFindAllResult, optionsService.showOptionsList());
        assertSame(optionList, actualFindAllResult);
    }

    /**
     * Method under test: {@link OptionsService#showOptionsListByPrefix(String)}
     */
    @Test
    void testShowOptionsListByPrefix() {
        // Arrange
        OptionsRepository optionsRepository = mock(OptionsRepository.class);
        when(optionsRepository.findByIdStartingWith(Mockito.<String>any())).thenReturn(new ArrayList<>());
        OptionMapper optionMapper = mock(OptionMapper.class);
        VolumeValidator volumeValidator = new VolumeValidator();
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

        // Act
        List<OptionDto> actualShowOptionsListByPrefixResult = (new OptionsService(optionsRepository, optionMapper,
                volumeValidator,
                new PositionSetter(null, new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                        passwordEncoder, new TextConverter()), mock(PortfoliosRepository.class))))
                .showOptionsListByPrefix("Prefix");

        // Assert
        verify(optionsRepository).findByIdStartingWith(eq("Prefix"));
        assertTrue(actualShowOptionsListByPrefixResult.isEmpty());
    }

    /**
     * Method under test: {@link OptionsService#showOptionsListByPrefix(String)}
     */
    @Test
    void testShowOptionsListByPrefix2() {
        // Arrange
        Option option = new Option();
        option.setBuyCollateral(10.0d);
        option.setDaysToMaturity(14);
        option.setId("42");
        option.setPortfolio(new ArrayList<>());
        option.setPrice(10.0d);
        option.setStepPrice(10.0d);
        option.setStrike(14);
        option.setType("Type");
        option.setVolatility(10.0d);
        option.setWriteCollateral(10.0d);

        ArrayList<Option> optionList = new ArrayList<>();
        optionList.add(option);
        OptionsRepository optionsRepository = mock(OptionsRepository.class);
        when(optionsRepository.findByIdStartingWith(Mockito.<String>any())).thenReturn(optionList);

        OptionDto optionDto = new OptionDto();
        optionDto.setBuyCollateral(10.0d);
        optionDto.setBuyOrWrite(19088743);
        optionDto.setDaysToMaturity(1);
        optionDto.setId("42");
        optionDto.setPrice(10.0d);
        optionDto.setStepPrice(10.0d);
        optionDto.setStrike(1);
        optionDto.setType("Type");
        optionDto.setVolatility(10.0d);
        optionDto.setVolume(1);
        optionDto.setWriteCollateral(10.0d);
        OptionMapper optionMapper = mock(OptionMapper.class);
        when(optionMapper.convertToOptionDto(Mockito.<Option>any())).thenReturn(optionDto);
        VolumeValidator volumeValidator = new VolumeValidator();
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

        // Act
        List<OptionDto> actualShowOptionsListByPrefixResult = (new OptionsService(optionsRepository, optionMapper,
                volumeValidator,
                new PositionSetter(null, new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                        passwordEncoder, new TextConverter()), mock(PortfoliosRepository.class))))
                .showOptionsListByPrefix("Prefix");

        // Assert
        verify(optionsRepository).findByIdStartingWith(eq("Prefix"));
        verify(optionMapper).convertToOptionDto(isA(Option.class));
        assertEquals(1, actualShowOptionsListByPrefixResult.size());
        assertSame(optionDto, actualShowOptionsListByPrefixResult.get(0));
    }

    /**
     * Method under test: {@link OptionsService#showOptionsListByPrefix(String)}
     */
    @Test
    void testShowOptionsListByPrefix3() {
        // Arrange
        Option option = new Option();
        option.setBuyCollateral(10.0d);
        option.setDaysToMaturity(14);
        option.setId("42");
        option.setPortfolio(new ArrayList<>());
        option.setPrice(10.0d);
        option.setStepPrice(10.0d);
        option.setStrike(14);
        option.setType("Type");
        option.setVolatility(10.0d);
        option.setWriteCollateral(10.0d);

        Option option2 = new Option();
        option2.setBuyCollateral(0.0d);
        option2.setDaysToMaturity(1);
        option2.setId("Option ticker: {}");
        option2.setPortfolio(new ArrayList<>());
        option2.setPrice(0.0d);
        option2.setStepPrice(0.0d);
        option2.setStrike(1);
        option2.setType("Price in RUR: {}");
        option2.setVolatility(0.0d);
        option2.setWriteCollateral(0.0d);

        ArrayList<Option> optionList = new ArrayList<>();
        optionList.add(option2);
        optionList.add(option);
        OptionsRepository optionsRepository = mock(OptionsRepository.class);
        when(optionsRepository.findByIdStartingWith(Mockito.<String>any())).thenReturn(optionList);

        OptionDto optionDto = new OptionDto();
        optionDto.setBuyCollateral(10.0d);
        optionDto.setBuyOrWrite(19088743);
        optionDto.setDaysToMaturity(1);
        optionDto.setId("42");
        optionDto.setPrice(10.0d);
        optionDto.setStepPrice(10.0d);
        optionDto.setStrike(1);
        optionDto.setType("Type");
        optionDto.setVolatility(10.0d);
        optionDto.setVolume(1);
        optionDto.setWriteCollateral(10.0d);
        OptionMapper optionMapper = mock(OptionMapper.class);
        when(optionMapper.convertToOptionDto(Mockito.<Option>any())).thenReturn(optionDto);
        VolumeValidator volumeValidator = new VolumeValidator();
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

        // Act
        List<OptionDto> actualShowOptionsListByPrefixResult = (new OptionsService(optionsRepository, optionMapper,
                volumeValidator,
                new PositionSetter(null, new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                        passwordEncoder, new TextConverter()), mock(PortfoliosRepository.class))))
                .showOptionsListByPrefix("Prefix");

        // Assert
        verify(optionsRepository).findByIdStartingWith(eq("Prefix"));
        verify(optionMapper, atLeast(1)).convertToOptionDto(Mockito.<Option>any());
        assertEquals(2, actualShowOptionsListByPrefixResult.size());
        assertSame(optionDto, actualShowOptionsListByPrefixResult.get(0));
        assertSame(optionDto, actualShowOptionsListByPrefixResult.get(1));
    }

    /**
     * Method under test: {@link OptionsService#showOptionsListByPrefix(String)}
     */
    @Test
    void testShowOptionsListByPrefix4() {
        // Arrange
        Option option = new Option();
        option.setBuyCollateral(10.0d);
        option.setDaysToMaturity(-1);
        option.setId("42");
        option.setPortfolio(new ArrayList<>());
        option.setPrice(10.0d);
        option.setStepPrice(10.0d);
        option.setStrike(14);
        option.setType("Type");
        option.setVolatility(10.0d);
        option.setWriteCollateral(10.0d);

        ArrayList<Option> optionList = new ArrayList<>();
        optionList.add(option);
        OptionsRepository optionsRepository = mock(OptionsRepository.class);
        when(optionsRepository.findByIdStartingWith(Mockito.<String>any())).thenReturn(optionList);
        OptionMapper optionMapper = mock(OptionMapper.class);
        VolumeValidator volumeValidator = new VolumeValidator();
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

        // Act
        List<OptionDto> actualShowOptionsListByPrefixResult = (new OptionsService(optionsRepository, optionMapper,
                volumeValidator,
                new PositionSetter(null, new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                        passwordEncoder, new TextConverter()), mock(PortfoliosRepository.class))))
                .showOptionsListByPrefix("Prefix");

        // Assert
        verify(optionsRepository).findByIdStartingWith(eq("Prefix"));
        assertTrue(actualShowOptionsListByPrefixResult.isEmpty());
    }

    /**
     * Method under test:
     * {@link OptionsService#showOptionsFromCurrentPortfolios(List)}
     */
    @Test
    void testShowOptionsFromCurrentPortfolios() {
        // Arrange
        OptionsRepository optionsRepository = mock(OptionsRepository.class);
        OptionMapper optionMapper = mock(OptionMapper.class);
        VolumeValidator volumeValidator = new VolumeValidator();
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
        OptionsService optionsService = new OptionsService(optionsRepository, optionMapper, volumeValidator,
                new PositionSetter(null, new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                        passwordEncoder, new TextConverter()), mock(PortfoliosRepository.class)));

        // Act and Assert
        assertTrue(optionsService.showOptionsFromCurrentPortfolios(new ArrayList<>()).isEmpty());
    }

    /**
     * Method under test:
     * {@link OptionsService#showOptionsFromCurrentPortfolios(List)}
     */
    @Test
    void testShowOptionsFromCurrentPortfolios2() {
        // Arrange
        Option option = new Option();
        option.setBuyCollateral(10.0d);
        option.setDaysToMaturity(1);
        option.setId("42");
        option.setPortfolio(new ArrayList<>());
        option.setPrice(10.0d);
        option.setStepPrice(10.0d);
        option.setStrike(1);
        option.setType("Type");
        option.setVolatility(10.0d);
        option.setWriteCollateral(10.0d);
        OptionsRepository optionsRepository = mock(OptionsRepository.class);
        when(optionsRepository.findOneById(Mockito.<String>any())).thenReturn(option);

        OptionDto optionDto = new OptionDto();
        optionDto.setBuyCollateral(10.0d);
        optionDto.setBuyOrWrite(19088743);
        optionDto.setDaysToMaturity(1);
        optionDto.setId("42");
        optionDto.setPrice(10.0d);
        optionDto.setStepPrice(10.0d);
        optionDto.setStrike(1);
        optionDto.setType("Type");
        optionDto.setVolatility(10.0d);
        optionDto.setVolume(1);
        optionDto.setWriteCollateral(10.0d);
        OptionMapper optionMapper = mock(OptionMapper.class);
        when(optionMapper.convertToOptionDto(Mockito.<Option>any())).thenReturn(optionDto);
        VolumeValidator volumeValidator = new VolumeValidator();
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
        OptionsService optionsService = new OptionsService(optionsRepository, optionMapper, volumeValidator,
                new PositionSetter(null, new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                        passwordEncoder, new TextConverter()), mock(PortfoliosRepository.class)));

        PortfolioDto portfolioDto = new PortfolioDto();
        portfolioDto.setBuyCollateral(10.0d);
        portfolioDto.setCollateralWhenWasTrade(10.0d);
        portfolioDto.setCurrentNetPosition(10.0d);
        portfolioDto.setDaysToMaturity(1);
        portfolioDto.setOpenLimit(10.0d);
        portfolioDto.setPrice(10.0d);
        portfolioDto.setStepPrice(10.0d);
        portfolioDto.setStrike(1);
        portfolioDto.setTradePrice(10.0d);
        portfolioDto.setType("Type");
        portfolioDto.setVariatMargin(10.0d);
        portfolioDto.setVolatility(10.0d);
        portfolioDto.setVolatilityWhenWasTrade(10.0d);
        portfolioDto.setVolume(1);
        portfolioDto.setWriteCollateral(10.0d);
        portfolioDto.setId("Portfolios");

        ArrayList<PortfolioDto> portfolios = new ArrayList<>();
        portfolios.add(portfolioDto);

        // Act
        List<OptionDto> actualShowOptionsFromCurrentPortfoliosResult = optionsService
                .showOptionsFromCurrentPortfolios(portfolios);

        // Assert
        verify(optionsRepository).findOneById(eq("Portfolios"));
        verify(optionMapper).convertToOptionDto(isA(Option.class));
        assertEquals(1, actualShowOptionsFromCurrentPortfoliosResult.size());
        assertSame(optionDto, actualShowOptionsFromCurrentPortfoliosResult.get(0));
    }

    /**
     * Method under test:
     * {@link OptionsService#showOptionsFromCurrentPortfolios(List)}
     */
    @Test
    void testShowOptionsFromCurrentPortfolios3() {
        // Arrange
        Option option = mock(Option.class);
        doNothing().when(option).setBuyCollateral(anyDouble());
        doNothing().when(option).setDaysToMaturity(anyInt());
        doNothing().when(option).setId(Mockito.<String>any());
        doNothing().when(option).setPortfolio(Mockito.<List<Portfolio>>any());
        doNothing().when(option).setPrice(anyDouble());
        doNothing().when(option).setStepPrice(anyDouble());
        doNothing().when(option).setStrike(anyInt());
        doNothing().when(option).setType(Mockito.<String>any());
        doNothing().when(option).setVolatility(anyDouble());
        doNothing().when(option).setWriteCollateral(anyDouble());
        option.setBuyCollateral(10.0d);
        option.setDaysToMaturity(1);
        option.setId("42");
        option.setPortfolio(new ArrayList<>());
        option.setPrice(10.0d);
        option.setStepPrice(10.0d);
        option.setStrike(1);
        option.setType("Type");
        option.setVolatility(10.0d);
        option.setWriteCollateral(10.0d);
        OptionsRepository optionsRepository = mock(OptionsRepository.class);
        when(optionsRepository.findOneById(Mockito.<String>any())).thenReturn(option);

        OptionDto optionDto = new OptionDto();
        optionDto.setBuyCollateral(10.0d);
        optionDto.setBuyOrWrite(19088743);
        optionDto.setDaysToMaturity(1);
        optionDto.setId("42");
        optionDto.setPrice(10.0d);
        optionDto.setStepPrice(10.0d);
        optionDto.setStrike(1);
        optionDto.setType("Type");
        optionDto.setVolatility(10.0d);
        optionDto.setVolume(1);
        optionDto.setWriteCollateral(10.0d);
        OptionMapper optionMapper = mock(OptionMapper.class);
        when(optionMapper.convertToOptionDto(Mockito.<Option>any())).thenReturn(optionDto);
        PositionSetter positionSetter = mock(PositionSetter.class);
        when(positionSetter.countPriceInRur(Mockito.<Option>any())).thenReturn(10.0d);
        OptionsService optionsService = new OptionsService(optionsRepository, optionMapper, new VolumeValidator(),
                positionSetter);

        PortfolioDto portfolioDto = new PortfolioDto();
        portfolioDto.setBuyCollateral(10.0d);
        portfolioDto.setCollateralWhenWasTrade(10.0d);
        portfolioDto.setCurrentNetPosition(10.0d);
        portfolioDto.setDaysToMaturity(1);
        portfolioDto.setOpenLimit(10.0d);
        portfolioDto.setPrice(10.0d);
        portfolioDto.setStepPrice(10.0d);
        portfolioDto.setStrike(1);
        portfolioDto.setTradePrice(10.0d);
        portfolioDto.setType("Type");
        portfolioDto.setVariatMargin(10.0d);
        portfolioDto.setVolatility(10.0d);
        portfolioDto.setVolatilityWhenWasTrade(10.0d);
        portfolioDto.setVolume(1);
        portfolioDto.setWriteCollateral(10.0d);
        portfolioDto.setId("Portfolios");

        ArrayList<PortfolioDto> portfolios = new ArrayList<>();
        portfolios.add(portfolioDto);

        // Act
        List<OptionDto> actualShowOptionsFromCurrentPortfoliosResult = optionsService
                .showOptionsFromCurrentPortfolios(portfolios);

        // Assert
        verify(option).setBuyCollateral(eq(10.0d));
        verify(option).setDaysToMaturity(eq(1));
        verify(option).setId(eq("42"));
        verify(option).setPortfolio(isA(List.class));
        verify(option).setPrice(eq(10.0d));
        verify(option).setStepPrice(eq(10.0d));
        verify(option).setStrike(eq(1));
        verify(option).setType(eq("Type"));
        verify(option).setVolatility(eq(10.0d));
        verify(option).setWriteCollateral(eq(10.0d));
        verify(optionsRepository).findOneById(eq("Portfolios"));
        verify(positionSetter).countPriceInRur(isA(Option.class));
        verify(optionMapper).convertToOptionDto(isA(Option.class));
        assertEquals(1, actualShowOptionsFromCurrentPortfoliosResult.size());
        assertSame(optionDto, actualShowOptionsFromCurrentPortfoliosResult.get(0));
    }

    /**
     * Method under test: {@link OptionsService#findByOptionId(String)}
     */
    @Test
    void testFindByOptionId() {
        // Arrange
        Option option = new Option();
        option.setBuyCollateral(10.0d);
        option.setDaysToMaturity(1);
        option.setId("42");
        option.setPortfolio(new ArrayList<>());
        option.setPrice(10.0d);
        option.setStepPrice(10.0d);
        option.setStrike(1);
        option.setType("Type");
        option.setVolatility(10.0d);
        option.setWriteCollateral(10.0d);
        OptionsRepository optionsRepository = mock(OptionsRepository.class);
        when(optionsRepository.findOneById(Mockito.<String>any())).thenReturn(option);
        OptionMapper optionMapper = mock(OptionMapper.class);
        VolumeValidator volumeValidator = new VolumeValidator();
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

        // Act
        Option actualFindByOptionIdResult = (new OptionsService(optionsRepository, optionMapper, volumeValidator,
                new PositionSetter(null, new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                        passwordEncoder, new TextConverter()), mock(PortfoliosRepository.class)))).findByOptionId("42");

        // Assert
        verify(optionsRepository).findOneById(eq("42"));
        assertSame(option, actualFindByOptionIdResult);
    }

    /**
     * Method under test: {@link OptionsService#convertToOptionDto(Option)}
     */
    @Test
    void testConvertToOptionDto() {
        // Arrange
        OptionDto optionDto = new OptionDto();
        optionDto.setBuyCollateral(10.0d);
        optionDto.setBuyOrWrite(19088743);
        optionDto.setDaysToMaturity(1);
        optionDto.setId("42");
        optionDto.setPrice(10.0d);
        optionDto.setStepPrice(10.0d);
        optionDto.setStrike(1);
        optionDto.setType("Type");
        optionDto.setVolatility(10.0d);
        optionDto.setVolume(1);
        optionDto.setWriteCollateral(10.0d);
        OptionMapper optionMapper = mock(OptionMapper.class);
        when(optionMapper.convertToOptionDto(Mockito.<Option>any())).thenReturn(optionDto);
        OptionsRepository optionsRepository = mock(OptionsRepository.class);
        VolumeValidator volumeValidator = new VolumeValidator();
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
        OptionsService optionsService = new OptionsService(optionsRepository, optionMapper, volumeValidator,
                new PositionSetter(null, new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                        passwordEncoder, new TextConverter()), mock(PortfoliosRepository.class)));

        Option option = new Option();
        option.setBuyCollateral(10.0d);
        option.setDaysToMaturity(1);
        option.setId("42");
        option.setPortfolio(new ArrayList<>());
        option.setPrice(10.0d);
        option.setStepPrice(10.0d);
        option.setStrike(1);
        option.setType("Type");
        option.setVolatility(10.0d);
        option.setWriteCollateral(10.0d);

        // Act
        OptionDto actualConvertToOptionDtoResult = optionsService.convertToOptionDto(option);

        // Assert
        verify(optionMapper).convertToOptionDto(isA(Option.class));
        assertSame(optionDto, actualConvertToOptionDtoResult);
    }

    /**
     * Method under test: {@link OptionsService#showOptionsList()}
     */
    @Test
    void testShowOptionsList() {
        // Arrange
        OptionsRepository optionsRepository = mock(OptionsRepository.class);
        when(optionsRepository.findAll()).thenReturn(new ArrayList<>());
        OptionMapper optionMapper = mock(OptionMapper.class);
        VolumeValidator volumeValidator = new VolumeValidator();
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

        // Act
        List<OptionDto> actualShowOptionsListResult = (new OptionsService(optionsRepository, optionMapper, volumeValidator,
                new PositionSetter(null, new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                        passwordEncoder, new TextConverter()), mock(PortfoliosRepository.class)))).showOptionsList();

        // Assert
        verify(optionsRepository).findAll();
        assertTrue(actualShowOptionsListResult.isEmpty());
    }

    /**
     * Method under test: {@link OptionsService#showOptionsList()}
     */
    @Test
    void testShowOptionsList2() {
        // Arrange
        Option option = new Option();
        option.setBuyCollateral(10.0d);
        option.setDaysToMaturity(1);
        option.setId("42");
        option.setPortfolio(new ArrayList<>());
        option.setPrice(10.0d);
        option.setStepPrice(10.0d);
        option.setStrike(1);
        option.setType("Type");
        option.setVolatility(10.0d);
        option.setWriteCollateral(10.0d);

        ArrayList<Option> optionList = new ArrayList<>();
        optionList.add(option);
        OptionsRepository optionsRepository = mock(OptionsRepository.class);
        when(optionsRepository.findAll()).thenReturn(optionList);

        OptionDto optionDto = new OptionDto();
        optionDto.setBuyCollateral(10.0d);
        optionDto.setBuyOrWrite(19088743);
        optionDto.setDaysToMaturity(1);
        optionDto.setId("42");
        optionDto.setPrice(10.0d);
        optionDto.setStepPrice(10.0d);
        optionDto.setStrike(1);
        optionDto.setType("Type");
        optionDto.setVolatility(10.0d);
        optionDto.setVolume(1);
        optionDto.setWriteCollateral(10.0d);
        OptionMapper optionMapper = mock(OptionMapper.class);
        when(optionMapper.convertToOptionDto(Mockito.<Option>any())).thenReturn(optionDto);
        VolumeValidator volumeValidator = new VolumeValidator();
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

        // Act
        List<OptionDto> actualShowOptionsListResult = (new OptionsService(optionsRepository, optionMapper, volumeValidator,
                new PositionSetter(null, new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                        passwordEncoder, new TextConverter()), mock(PortfoliosRepository.class)))).showOptionsList();

        // Assert
        verify(optionMapper).convertToOptionDto(isA(Option.class));
        verify(optionsRepository).findAll();
        assertEquals(1, actualShowOptionsListResult.size());
        assertSame(optionDto, actualShowOptionsListResult.get(0));
    }

    /**
     * Method under test: {@link OptionsService#showOptionsList()}
     */
    @Test
    void testShowOptionsList3() {
        // Arrange
        Option option = new Option();
        option.setBuyCollateral(10.0d);
        option.setDaysToMaturity(1);
        option.setId("42");
        option.setPortfolio(new ArrayList<>());
        option.setPrice(10.0d);
        option.setStepPrice(10.0d);
        option.setStrike(1);
        option.setType("Type");
        option.setVolatility(10.0d);
        option.setWriteCollateral(10.0d);

        Option option2 = new Option();
        option2.setBuyCollateral(0.0d);
        option2.setDaysToMaturity(2);
        option2.setId("Option ticker: {}");
        option2.setPortfolio(new ArrayList<>());
        option2.setPrice(0.0d);
        option2.setStepPrice(0.0d);
        option2.setStrike(2);
        option2.setType("Price in RUR: {}");
        option2.setVolatility(0.0d);
        option2.setWriteCollateral(0.0d);

        ArrayList<Option> optionList = new ArrayList<>();
        optionList.add(option2);
        optionList.add(option);
        OptionsRepository optionsRepository = mock(OptionsRepository.class);
        when(optionsRepository.findAll()).thenReturn(optionList);

        OptionDto optionDto = new OptionDto();
        optionDto.setBuyCollateral(10.0d);
        optionDto.setBuyOrWrite(19088743);
        optionDto.setDaysToMaturity(1);
        optionDto.setId("42");
        optionDto.setPrice(10.0d);
        optionDto.setStepPrice(10.0d);
        optionDto.setStrike(1);
        optionDto.setType("Type");
        optionDto.setVolatility(10.0d);
        optionDto.setVolume(1);
        optionDto.setWriteCollateral(10.0d);
        OptionMapper optionMapper = mock(OptionMapper.class);
        when(optionMapper.convertToOptionDto(Mockito.<Option>any())).thenReturn(optionDto);
        VolumeValidator volumeValidator = new VolumeValidator();
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

        // Act
        List<OptionDto> actualShowOptionsListResult = (new OptionsService(optionsRepository, optionMapper, volumeValidator,
                new PositionSetter(null, new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                        passwordEncoder, new TextConverter()), mock(PortfoliosRepository.class)))).showOptionsList();

        // Assert
        verify(optionMapper, atLeast(1)).convertToOptionDto(Mockito.<Option>any());
        verify(optionsRepository).findAll();
        assertEquals(2, actualShowOptionsListResult.size());
        assertSame(optionDto, actualShowOptionsListResult.get(0));
        assertSame(optionDto, actualShowOptionsListResult.get(1));
    }

    /**
     * Method under test: {@link OptionsService#convertListToOptionDtoList(List)}
     */
    @Test
    void testConvertListToOptionDtoList() {
        // Arrange
        OptionsRepository optionsRepository = mock(OptionsRepository.class);
        OptionMapper optionMapper = mock(OptionMapper.class);
        VolumeValidator volumeValidator = new VolumeValidator();
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
        OptionsService optionsService = new OptionsService(optionsRepository, optionMapper, volumeValidator,
                new PositionSetter(null, new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                        passwordEncoder, new TextConverter()), mock(PortfoliosRepository.class)));

        // Act and Assert
        assertTrue(optionsService.convertListToOptionDtoList(new ArrayList<>()).isEmpty());
    }

    /**
     * Method under test: {@link OptionsService#convertListToOptionDtoList(List)}
     */
    @Test
    void testConvertListToOptionDtoList2() {
        // Arrange
        OptionDto optionDto = new OptionDto();
        optionDto.setBuyCollateral(10.0d);
        optionDto.setBuyOrWrite(19088743);
        optionDto.setDaysToMaturity(1);
        optionDto.setId("42");
        optionDto.setPrice(10.0d);
        optionDto.setStepPrice(10.0d);
        optionDto.setStrike(1);
        optionDto.setType("Type");
        optionDto.setVolatility(10.0d);
        optionDto.setVolume(1);
        optionDto.setWriteCollateral(10.0d);
        OptionMapper optionMapper = mock(OptionMapper.class);
        when(optionMapper.convertToOptionDto(Mockito.<Option>any())).thenReturn(optionDto);
        OptionsRepository optionsRepository = mock(OptionsRepository.class);
        VolumeValidator volumeValidator = new VolumeValidator();
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
        OptionsService optionsService = new OptionsService(optionsRepository, optionMapper, volumeValidator,
                new PositionSetter(null, new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                        passwordEncoder, new TextConverter()), mock(PortfoliosRepository.class)));

        Option option = new Option();
        option.setBuyCollateral(10.0d);
        option.setDaysToMaturity(1);
        option.setId("42");
        option.setPortfolio(new ArrayList<>());
        option.setPrice(10.0d);
        option.setStepPrice(10.0d);
        option.setStrike(1);
        option.setType("Type");
        option.setVolatility(10.0d);
        option.setWriteCollateral(10.0d);

        ArrayList<Option> options = new ArrayList<>();
        options.add(option);

        // Act
        List<OptionDto> actualConvertListToOptionDtoListResult = optionsService.convertListToOptionDtoList(options);

        // Assert
        verify(optionMapper).convertToOptionDto(isA(Option.class));
        assertEquals(1, actualConvertListToOptionDtoListResult.size());
        assertSame(optionDto, actualConvertListToOptionDtoListResult.get(0));
    }

    /**
     * Method under test: {@link OptionsService#convertListToOptionDtoList(List)}
     */
    @Test
    void testConvertListToOptionDtoList3() {
        // Arrange
        OptionDto optionDto = new OptionDto();
        optionDto.setBuyCollateral(10.0d);
        optionDto.setBuyOrWrite(19088743);
        optionDto.setDaysToMaturity(1);
        optionDto.setId("42");
        optionDto.setPrice(10.0d);
        optionDto.setStepPrice(10.0d);
        optionDto.setStrike(1);
        optionDto.setType("Type");
        optionDto.setVolatility(10.0d);
        optionDto.setVolume(1);
        optionDto.setWriteCollateral(10.0d);
        OptionMapper optionMapper = mock(OptionMapper.class);
        when(optionMapper.convertToOptionDto(Mockito.<Option>any())).thenReturn(optionDto);
        PositionSetter positionSetter = mock(PositionSetter.class);
        when(positionSetter.countPriceInRur(Mockito.<Option>any())).thenReturn(10.0d);
        OptionsRepository optionsRepository = mock(OptionsRepository.class);
        OptionsService optionsService = new OptionsService(optionsRepository, optionMapper, new VolumeValidator(),
                positionSetter);

        Option option = new Option();
        option.setBuyCollateral(10.0d);
        option.setDaysToMaturity(1);
        option.setId("42");
        option.setPortfolio(new ArrayList<>());
        option.setPrice(10.0d);
        option.setStepPrice(10.0d);
        option.setStrike(1);
        option.setType("Type");
        option.setVolatility(10.0d);
        option.setWriteCollateral(10.0d);

        ArrayList<Option> options = new ArrayList<>();
        options.add(option);

        // Act
        List<OptionDto> actualConvertListToOptionDtoListResult = optionsService.convertListToOptionDtoList(options);

        // Assert
        verify(positionSetter).countPriceInRur(isA(Option.class));
        verify(optionMapper).convertToOptionDto(isA(Option.class));
        assertEquals(1, actualConvertListToOptionDtoListResult.size());
        assertSame(optionDto, actualConvertListToOptionDtoListResult.get(0));
    }

    /**
     * Method under test:
     * {@link OptionsService#createOptionDtoFromView(String, int, int)}
     */
    @Test
    void testCreateOptionDtoFromView() {
        // Arrange
        OptionsRepository optionsRepository = mock(OptionsRepository.class);
        OptionMapper optionMapper = mock(OptionMapper.class);
        VolumeValidator volumeValidator = new VolumeValidator();
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

        // Act
        OptionDto actualCreateOptionDtoFromViewResult = (new OptionsService(optionsRepository, optionMapper,
                volumeValidator,
                new PositionSetter(null, new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                        passwordEncoder, new TextConverter()), mock(PortfoliosRepository.class)))).createOptionDtoFromView("42", 1,
                1);

        // Assert
        assertEquals("42", actualCreateOptionDtoFromViewResult.getId());
        assertNull(actualCreateOptionDtoFromViewResult.getType());
        assertEquals(0, actualCreateOptionDtoFromViewResult.getDaysToMaturity());
        assertEquals(0, actualCreateOptionDtoFromViewResult.getStrike());
        assertEquals(0.0d, actualCreateOptionDtoFromViewResult.getBuyCollateral());
        assertEquals(0.0d, actualCreateOptionDtoFromViewResult.getPrice());
        assertEquals(0.0d, actualCreateOptionDtoFromViewResult.getStepPrice());
        assertEquals(0.0d, actualCreateOptionDtoFromViewResult.getVolatility());
        assertEquals(0.0d, actualCreateOptionDtoFromViewResult.getWriteCollateral());
        assertEquals(1, actualCreateOptionDtoFromViewResult.getBuyOrWrite());
        assertEquals(1, actualCreateOptionDtoFromViewResult.getVolume());
    }
}
