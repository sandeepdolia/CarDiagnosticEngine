package com.ubiquisoft.evaluation;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.ubiquisoft.evaluation.domain.Car;
import com.ubiquisoft.evaluation.domain.ConditionType;
import com.ubiquisoft.evaluation.domain.Part;
import com.ubiquisoft.evaluation.domain.PartType;

public class CarDiagnosticEngine {

	public void executeDiagnostics(Car car) {
		/*
		 * Implement basic diagnostics and print results to console.
		 *
		 * The purpose of this method is to find any problems with a car's data or parts.
		 *
		 * Diagnostic Steps:
		 *      First   - Validate the 3 data fields are present, if one or more are
		 *                then print the missing fields to the console
		 *                in a similar manner to how the provided methods do.
		 *
		 *      Second  - Validate that no parts are missing using the 'getMissingPartsMap' method in the Car class,
		 *                if one or more are then run each missing part and its count through the provided missing part method.
		 *
		 *      Third   - Validate that all parts are in working condition, if any are not
		 *                then run each non-working part through the provided damaged part method.
		 *
		 *      Fourth  - If validation succeeds for the previous steps then print something to the console informing the user as such.
		 * A damaged part is one that has any condition other than NEW, GOOD, or WORN.
		 *
		 * Important:
		 *      If any validation fails, complete whatever step you are actively one and end diagnostics early.
		 *
		 * Treat the console as information being read by a user of this application. Attempts should be made to ensure
		 * console output is as least as informative as the provided methods.
		 */
		List<Part> parts = car.getParts();
		
		if(parts != null && !parts.isEmpty()) {
			Set<PartType> workingParts = new HashSet<PartType>();
			workingParts.add(PartType.ELECTRICAL);
			workingParts.add(PartType.ENGINE);
			workingParts.add(PartType.FUEL_FILTER);
			workingParts.add(PartType.OIL_FILTER);
			workingParts.add(PartType.TIRE);
			
			//# 1 Prints missing fields
			IntStream.range(1, parts.size()).forEach(idx -> printMissingField(parts.get(idx),idx ));
			//#2 Prints missing parts
			Map<PartType, Integer> missingPartsMap = car.getMissingPartsMap();
			if(!missingPartsMap.isEmpty()) {
				Set<PartType> keySet = missingPartsMap.keySet();
				keySet.forEach(partType->{ printMissingPart(partType, missingPartsMap.get(partType)); workingParts.remove(partType); } );
			}
			
			//# 3 Prints the damaged parts
			parts.forEach(part ->  { if(!part.isInWorkingCondition()) { printDamagedPart(part.getType(), part.getCondition()); workingParts.remove(part.getType());} });
			// #4 // 2 tires are working but I'm grouping this by partType. I can perhaps add a logic to be more specific on that,if needed.
			if(!workingParts.isEmpty())
				workingParts.forEach(workingPart -> System.out.println(String.format("Working Part : %s",workingPart)));
		}
	}

	private void printMissingPart(PartType partType, Integer count) {
		if (partType == null) throw new IllegalArgumentException("PartType must not be null");
		if (count == null || count <= 0) throw new IllegalArgumentException("Count must be greater than 0");
		
		System.out.println(String.format("Missing Part(s) Detected: %s - Count: %s", partType, count));
	}

	private void printMissingField(Part part, Integer count) {
		if (part.getInventoryId() == null) System.out.println(String.format("Missing InventoryId: - Count: %s",count));
		if(part.getInventoryId() != null) {
			if (part.getType() == null) System.out.println(String.format("Missing Type for InventoryId: %s - Count: %s", part.getInventoryId(), count));
			if (part.getCondition() == null) System.out.println(String.format("Missing condition for InventoryId: %s - Count: %s", part.getInventoryId(), count));
		 }
		}

	
	private void printDamagedPart(PartType partType, ConditionType condition) {
		if (partType == null) throw new IllegalArgumentException("PartType must not be null");
		if (condition == null) throw new IllegalArgumentException("ConditionType must not be null");

		System.out.println(String.format("Damaged Part Detected: %s - Condition: %s", partType, condition));
	}

	public static void main(String[] args) throws JAXBException {
		// Load classpath resource
		InputStream xml = ClassLoader.getSystemResourceAsStream("SampleCar.xml");

		// Verify resource was loaded properly
		if (xml == null) {
			System.err.println("An error occurred attempting to load SampleCar.xml");

			System.exit(1);
		}

		// Build JAXBContext for converting XML into an Object
		JAXBContext context = JAXBContext.newInstance(Car.class, Part.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();

		Car car = (Car) unmarshaller.unmarshal(xml);

		// Build new Diagnostics Engine and execute on deserialized car object.

		CarDiagnosticEngine diagnosticEngine = new CarDiagnosticEngine();

		diagnosticEngine.executeDiagnostics(car);

	}

}
