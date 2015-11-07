package com.nikola.amazon.stanalyzer.util;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nikola.amazon.stanalyzer.entity.StanCena;

public class StanCenaJsonSerializer extends JsonSerializer<List<StanCena>>{

	@Override
	public void serialize(List<StanCena> arg0, JsonGenerator arg1,
			SerializerProvider arg2) throws IOException,
			JsonProcessingException {
		StringBuilder builder = new StringBuilder("");
		for(StanCena cena : arg0) {
			if(arg0.indexOf(cena) != (arg0.size()-1)) {
				builder.append(cena.getIznos()).append(",");
			}
			else {
				builder.append(cena.getIznos());
			}
		}
		arg1.writeString(builder.toString());
		
	}



}
