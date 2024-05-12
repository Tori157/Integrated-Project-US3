package sit.int221.backend.service;


import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class ListMapper {

    private static final ListMapper listMapper = new ListMapper();

    private static ModelMapper modelMapper = new ModelMapper();

    private ListMapper() {
    }

    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass, ModelMapper modelMapper) {
        return source.stream().map(entity -> modelMapper.map(entity, targetClass))
                .collect(Collectors.toList());
    }


    public static ListMapper getInstance() {
        return listMapper;
    }

}
