/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.mapping.model;

import static org.assertj.core.api.Assertions.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import lombok.experimental.Wither;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.Test;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentPropertyPath;
import org.springframework.data.mapping.PersistentPropertyPathAccessor;
import org.springframework.data.mapping.context.SampleMappingContext;
import org.springframework.data.mapping.context.SamplePersistentProperty;

/**
 * @author Oliver Gierke
 * @since 2.2
 */
public class SimplePersistentPropertyPathAccessorUnitTests {

	SampleMappingContext context = new SampleMappingContext();

	Customer first = new Customer("1");
	Customer second = new Customer("2");

	@Test
	public void setsPropertyContainingCollectionPathForAllElements() {

		Customers customers = new Customers(Arrays.asList(first, second), Collections.emptyMap());

		assertFirstnamesSetFor(customers, "customers.firstname");
	}

	@Test
	public void setsPropertyContainingMapPathForAllValues() {

		Map<String, Customer> map = new HashMap<>();
		map.put("1", first);
		map.put("2", second);

		Customers customers = new Customers(Collections.emptyList(), map);

		assertFirstnamesSetFor(customers, "customerMap.firstname");
	}

	private void assertFirstnamesSetFor(Customers customers, String path) {

		PersistentEntity<Object, SamplePersistentProperty> entity = context.getRequiredPersistentEntity(Customers.class);

		PersistentPropertyPathAccessor<Customers> accessor = entity.getPropertyPathAccessor(customers);
		PersistentPropertyPath<SamplePersistentProperty> propertyPath = context.getPersistentPropertyPath(path,
				Customers.class);

		accessor.setProperty(propertyPath, "firstname");

		Stream.of(first, second).forEach(it -> {
			assertThat(it.firstname).isEqualTo("firstname");
		});
	}

	@Data
	@AllArgsConstructor
	static class Customer {
		String firstname;
	}

	@Value
	static class Customers {
		@Wither List<Customer> customers;
		@Wither Map<String, Customer> customerMap;
	}
}
