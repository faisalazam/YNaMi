package pk.lucidxpo.ynami.persistence.model;

import org.junit.Test;
import org.modelmapper.ModelMapper;
import pk.lucidxpo.ynami.persistence.dto.SampleCreationDTO;
import pk.lucidxpo.ynami.persistence.dto.SampleUpdateStatusDTO;
import pk.lucidxpo.ynami.persistence.dto.SampleUpdationDTO;

import static java.lang.Long.valueOf;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static pk.lucidxpo.ynami.testutils.Identity.randomInt;
import static pk.lucidxpo.ynami.testutils.Randomly.chooseOneOf;

public class EntityAndDtoConversionTest {
    private ModelMapper modelMapper = new ModelMapper();

    @Test
    public void shouldConvertSampleEntityToSampleCreationDtoCorrectly() {
        Sample sample = Sample.builder()
                .id(valueOf(randomInt()))
                .active(chooseOneOf(true, false))
                .address(randomAlphabetic(50))
                .firstName(randomAlphabetic(10))
                .lastName(randomAlphabetic(10))
                .build();

        SampleCreationDTO convertedSampleCreationDto = modelMapper.map(sample, SampleCreationDTO.class);
        assertThat(convertedSampleCreationDto.isActive(), is(sample.isActive()));
        assertThat(convertedSampleCreationDto.getAddress(), is(sample.getAddress()));
        assertThat(convertedSampleCreationDto.getFirstName(), is(sample.getFirstName()));
        assertThat(convertedSampleCreationDto.getLastName(), is(sample.getLastName()));
    }

    @Test
    public void shouldVerifyConversionsBetweenEntityAndDtos() {
        SampleCreationDTO sampleCreationDTO = SampleCreationDTO.builder()
                .active(chooseOneOf(true, false))
                .address(randomAlphabetic(50))
                .firstName(randomAlphabetic(10))
                .lastName(randomAlphabetic(10))
                .build();

        Sample convertedSample = modelMapper.map(sampleCreationDTO, Sample.class);
        assertThat(convertedSample.isActive(), is(sampleCreationDTO.isActive()));
        assertThat(convertedSample.getAddress(), is(sampleCreationDTO.getAddress()));
        assertThat(convertedSample.getFirstName(), is(sampleCreationDTO.getFirstName()));
        assertThat(convertedSample.getLastName(), is(sampleCreationDTO.getLastName()));

        SampleUpdationDTO sampleUpdationDto = SampleUpdationDTO.builder()
                .active(chooseOneOf(true, false))
                .firstName(randomAlphabetic(10))
                .lastName(randomAlphabetic(10))
                .build();

        modelMapper.map(sampleUpdationDto, convertedSample);
        assertThat(convertedSample.isActive(), is(sampleUpdationDto.isActive()));
        assertThat(convertedSample.getAddress(), is(sampleCreationDTO.getAddress()));
        assertThat(convertedSample.getFirstName(), is(sampleUpdationDto.getFirstName()));
        assertThat(convertedSample.getLastName(), is(sampleUpdationDto.getLastName()));

        convertedSample.setActive(false);

        SampleUpdateStatusDTO sampleUpdateStatusDTO = SampleUpdateStatusDTO.builder()
                .active(true)
                .build();

        modelMapper.map(sampleUpdateStatusDTO, convertedSample);
        assertThat(convertedSample.isActive(), is(sampleUpdateStatusDTO.isActive()));
        assertThat(convertedSample.getAddress(), is(sampleCreationDTO.getAddress()));
        assertThat(convertedSample.getFirstName(), is(sampleUpdationDto.getFirstName()));
        assertThat(convertedSample.getLastName(), is(sampleUpdationDto.getLastName()));
    }
}