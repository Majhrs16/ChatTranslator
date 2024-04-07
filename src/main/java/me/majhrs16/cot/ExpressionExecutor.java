package me.majhrs16.cot;

import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.MapContext; // Import MapContext

import java.util.Map;

public class ExpressionExecutor {
	public Object invoke(Map<String, Object> contextMap, String expression) {
		JexlContext jexlContext = new MapContext();
		JexlEngine jexlEngine = new JexlBuilder()
			.strict(true)
			.create();

		for (Map.Entry<String, Object> entry : contextMap.entrySet()) {
			jexlContext.set(entry.getKey(), entry.getValue());
		}

		JexlExpression jexlExpression = jexlEngine.createExpression(expression);
		return jexlExpression.evaluate(jexlContext);
	}
}
