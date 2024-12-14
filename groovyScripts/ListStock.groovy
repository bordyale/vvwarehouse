/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.ofbiz.entity.condition.EntityExpr
import org.apache.ofbiz.entity.condition.EntityFunction
import org.apache.ofbiz.entity.condition.EntityOperator
import org.apache.ofbiz.entity.condition.EntityFieldValue
import org.apache.ofbiz.entity.condition.EntityConditionList
import org.apache.ofbiz.entity.condition.EntityCondition
import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.entity.util.EntityUtil
import java.sql.Timestamp
import java.text.SimpleDateFormat
import org.apache.ofbiz.base.util.UtilDateTime

def sdf = new SimpleDateFormat("yyyy-MM-dd")

filstatus = parameters.filstatus
filproductId = parameters.filproductId
fildate1From = parameters.fildate1From
fildate2From = parameters.fildate2From


List searchCond = []

if (filproductId) {
	searchCond.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, filproductId))
}
if (filstatus) {
	searchCond.add(EntityCondition.makeCondition("status", EntityOperator.EQUALS, filstatus))
}
if (fildate1From) {
	def parseDate = sdf.parse(fildate1From)
	searchCond.add(EntityCondition.makeCondition("date", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toTimestamp(parseDate)))
}
if (fildate2From) {
	def parseDate = sdf.parse(fildate2From)
	searchCond.add(EntityCondition.makeCondition("date", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toTimestamp(parseDate)))
}

filterResult = from("VvWarehouse").where(searchCond).cache(false).queryList()

filterList = EntityUtil.orderBy(filterResult,  ["-date"])

List<HashMap<String,Object>> hashMaps = new ArrayList<HashMap<String,Object>>()
for (GenericValue entry: filterList){
	Map<String,Object> e = new HashMap<String,Object>()
	e.put("whId",entry.get("whId"))
	e.put("productId",entry.get("productId"))
	e.put("shipmentId",entry.get("shipmentId"))
	e.put("rack",entry.get("rack"))
	e.put("quantity",entry.get("quantity"))
	e.put("date",entry.get("date"))
	e.put("status",entry.get("status"))
	e.put("sn",entry.get("sn"))
	hashMaps.add(e)
}



context.listResult = hashMaps
