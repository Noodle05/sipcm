@TypeDefs(
		{
			@TypeDef(
					name = "encryptedString",
					typeClass = EncryptedStringType.class,
					parameters = {
						@Parameter(name = "encryptorRegisteredName", value = "sipHibernateStringEncryptor")
						}
					)
					}
		)
package com.sipcm;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.jasypt.hibernate.type.EncryptedStringType;

