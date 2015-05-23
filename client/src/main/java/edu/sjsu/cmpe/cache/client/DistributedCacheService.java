package edu.sjsu.cmpe.cache.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Distributed cache service
 * 
 */
public class DistributedCacheService implements CacheServiceInterface {
	private final String cacheServerUrl1;
	private final String cacheServerUrl2;
	private final String cacheServerUrl3;

	int success = 0;
	int fail = 0;

	public DistributedCacheService(String serverUrl1, String serverUrl2,
			String serverUrl3) {
		this.cacheServerUrl1 = serverUrl1;
		this.cacheServerUrl2 = serverUrl2;
		this.cacheServerUrl3 = serverUrl3;
	}

	/**
	 * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#get(long)
	 */
	@Override
	public String get(long key) {
		final Map<String, String> rm = new HashMap<String, String>();
		String r1 = "";
		String r2 = "";
		String r3 = "";
		Future<HttpResponse<JsonNode>> d1 = Unirest
				.get(this.cacheServerUrl1 + "/cache/{key}")
				.header("accept", "application/json")
				.routeParam("key", Long.toString(key))
				.asJsonAsync(new Callback<JsonNode>() {

					public void failed(UnirestException e) {
						rm.put("r1", "FAILED1");
					}

					public void completed(HttpResponse<JsonNode> response) {
						rm.put("r1",
								response.getBody().getObject()
										.getString("value"));
					}

					public void cancelled() {
					}
				});
		Future<HttpResponse<JsonNode>> d2 = Unirest
				.get(this.cacheServerUrl2 + "/cache/{key}")
				.header("accept", "application/json")
				.routeParam("key", Long.toString(key))
				.asJsonAsync(new Callback<JsonNode>() {

					public void failed(UnirestException e) {
						rm.put("r2", "FAILED2");
					}

					public void completed(HttpResponse<JsonNode> response) {
						rm.put("r2",
								response.getBody().getObject()
										.getString("value"));
					}

					public void cancelled() {
					}
				});
		Future<HttpResponse<JsonNode>> d3 = Unirest
				.get(this.cacheServerUrl3 + "/cache/{key}")
				.header("accept", "application/json")
				.routeParam("key", Long.toString(key))
				.asJsonAsync(new Callback<JsonNode>() {

					public void failed(UnirestException e) {
						rm.put("r3", "FAILED3");
					}

					public void completed(HttpResponse<JsonNode> response) {
						rm.put("r3",
								response.getBody().getObject()
										.getString("value"));
					}

					public void cancelled() {
					}
				});
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		r1 = rm.containsKey("r1") ? rm.get("r1") : "";
		r2 = rm.containsKey("r2") ? rm.get("r2") : "";
		r3 = rm.containsKey("r3") ? rm.get("r3") : "";
		String value = null;
		if (r1.equals(r2) && r2.equals(r3)) {
			value = r1;
		} else {
			if (r1.equals(r2)) {
				System.out.println("Fixing data in " + cacheServerUrl3);
				// set to r3
				put(key, r1, cacheServerUrl3);
				value = r1;
			} else if (r2.equals(r3)) {
				System.out.println("Fixing data in " + cacheServerUrl1);
				// set to r1
				put(key, r2, cacheServerUrl1);
				value = r2;
			} else if (r1.equals(r3)) {
				System.out.println("Fixing data in " + cacheServerUrl2);
				// set to r2
				put(key, r1, cacheServerUrl2);
				value = r1;
			}
		}
		return value;
	}

	@Override
	public void put(long key, String value) {
		success = 0;
		fail = 0;
		Future<HttpResponse<JsonNode>> future1 = Unirest
				.put(this.cacheServerUrl1 + "/cache/{key}/{value}")
				.header("accept", "application/json")
				.routeParam("key", Long.toString(key))
				.routeParam("value", value)
				.asJsonAsync(new Callback<JsonNode>() {

					public void failed(UnirestException e) {
						fail++;
					}

					public void completed(HttpResponse<JsonNode> response) {
						success++;
					}

					public void cancelled() {
						System.out.println("The request has been cancelled");
					}

				});
		Future<HttpResponse<JsonNode>> future2 = Unirest
				.put(this.cacheServerUrl2 + "/cache/{key}/{value}")
				.header("accept", "application/json")
				.routeParam("key", Long.toString(key))
				.routeParam("value", value)
				.asJsonAsync(new Callback<JsonNode>() {

					public void failed(UnirestException e) {
						fail++;
					}

					public void completed(HttpResponse<JsonNode> response) {
						success++;
					}

					public void cancelled() {
						System.out.println("The request has been cancelled");
					}

				});
		Future<HttpResponse<JsonNode>> future3 = Unirest
				.put(this.cacheServerUrl3 + "/cache/{key}/{value}")
				.header("accept", "application/json")
				.routeParam("key", Long.toString(key))
				.routeParam("value", value)
				.asJsonAsync(new Callback<JsonNode>() {

					public void failed(UnirestException e) {
						fail++;
					}

					public void completed(HttpResponse<JsonNode> response) {
						success++;
					}

					public void cancelled() {
						System.out.println("The request has been cancelled");
					}

				});
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (fail > 1) {
			System.out.println("Deleting the entries");
			Future<HttpResponse<JsonNode>> d1 = Unirest
					.delete(this.cacheServerUrl1 + "/cache/{key}")
					.header("accept", "application/json")
					.routeParam("key", Long.toString(key))
					.asJsonAsync(new Callback<JsonNode>() {

						public void failed(UnirestException e) {
						}

						public void completed(HttpResponse<JsonNode> response) {
						}

						public void cancelled() {
						}
					});
			Future<HttpResponse<JsonNode>> d2 = Unirest
					.delete(this.cacheServerUrl2 + "/cache/{key}")
					.header("accept", "application/json")
					.routeParam("key", Long.toString(key))
					.asJsonAsync(new Callback<JsonNode>() {

						public void failed(UnirestException e) {
						}

						public void completed(HttpResponse<JsonNode> response) {
						}

						public void cancelled() {
						}
					});
			Future<HttpResponse<JsonNode>> d3 = Unirest
					.delete(this.cacheServerUrl3 + "/cache/{key}")
					.header("accept", "application/json")
					.routeParam("key", Long.toString(key))
					.asJsonAsync(new Callback<JsonNode>() {

						public void failed(UnirestException e) {
						}

						public void completed(HttpResponse<JsonNode> response) {
						}

						public void cancelled() {
						}
					});
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/**
	 * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#put(long,
	 *      java.lang.String)
	 */
	public void put(long key, String value, String server) {
		HttpResponse<JsonNode> response = null;
		try {
			response = Unirest.put(server + "/cache/{key}/{value}")
					.header("accept", "application/json")
					.routeParam("key", Long.toString(key))
					.routeParam("value", value).asJson();
		} catch (UnirestException e) {
			System.err.println(e);
		}

		if (response.getCode() != 200) {
			System.out.println("Failed to add to the cache.");
		}
	}
}
