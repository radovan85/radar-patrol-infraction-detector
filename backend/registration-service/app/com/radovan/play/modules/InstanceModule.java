package com.radovan.play.modules;

import com.google.inject.AbstractModule;
import com.radovan.play.providers.HibernateProvider;
import com.radovan.play.providers.NatsUtilsProvider;
import com.radovan.play.utils.NatsUtils;
import org.hibernate.SessionFactory;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

public class InstanceModule extends AbstractModule {

    @Override
    protected void configure(){
        bind(SessionFactory.class).toProvider(HibernateProvider.class).asEagerSingleton();
        bind(NatsUtils.class).toProvider(NatsUtilsProvider.class).asEagerSingleton();
        bind(ModelMapper.class).toInstance(getMapper());
    }

    public ModelMapper getMapper() {
        ModelMapper returnValue = new ModelMapper();
        returnValue.getConfiguration().setAmbiguityIgnored(true).setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
        returnValue.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return returnValue;
    }
}
