/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.cloud.autoconfigure.servicebus;

import com.microsoft.azure.servicebus.TopicClient;
import com.microsoft.azure.spring.integration.servicebus.factory.ServiceBusTopicClientFactory;
import com.microsoft.azure.spring.integration.servicebus.topic.ServiceBusTopicOperation;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class AzureServiceBusTopicAutoConfigurationTest {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AzureServiceBusTopicAutoConfiguration.class))
            .withUserConfiguration(ServiceBusTestConfiguration.class);

    @Test
    public void testAzureServiceBusTopicDisabled() {
        this.contextRunner.withPropertyValues("spring.cloud.azure.servicebus.topic.enabled=false")
                          .run(context -> assertThat(context).doesNotHaveBean(AzureServiceBusProperties.class));
    }

    @Test
    public void testWithoutAzureServiceBusTopicClient() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(TopicClient.class))
                          .run(context -> assertThat(context).doesNotHaveBean(AzureServiceBusProperties.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testAzureServiceBusPropertiesNamespaceIllegal() {
        this.contextRunner.withPropertyValues("spring.cloud.azure.servicebus.namespace=")
                .run(context -> context.getBean(AzureServiceBusProperties.class));
    }

    @Test
    public void testAzureServiceBusPropertiesConfigured() {
        this.contextRunner.withPropertyValues("spring.cloud.azure.servicebus.namespace=ns1").run(context -> {
            assertThat(context).hasSingleBean(AzureServiceBusProperties.class);
            assertThat(context.getBean(AzureServiceBusProperties.class).getNamespace()).isEqualTo("ns1");
            assertThat(context).hasSingleBean(ServiceBusTopicClientFactory.class);
            assertThat(context).hasSingleBean(ServiceBusTopicOperation.class);
        });
    }
}
