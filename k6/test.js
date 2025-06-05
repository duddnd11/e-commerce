import http from 'k6/http';
import {check, sleep} from 'k6';
import {randomIntBetween} from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export let options = {
  scenarios: {
    order_scenario: {
      executor: 'ramping-vus',
      exec: 'order_scenario',
      startVUs: 0,
      stages: [
        { duration: "30s", target: 50 },
        { duration: "1m", target: 100 },
        { duration: "2m", target: 200 },
      ],
    },
  },
};


export function order_scenario() {
	
    // userId를 1부터 500 사이에서 랜덤으로 생성
    let userId = randomIntBetween(1, 100);

    couponIssue(userId);
    
    sleep(2);
    
   	let orderReponse = order(userId);
   	
    sleep(10);
    
   	payment(userId, orderReponse);
   	
    sleep(2);
   	
   	getTopProduct();
}

function couponIssue(userId){
    let payload = JSON.stringify({
		userId: userId,
        couponId: 1
    });
    
    let headers = {
        headers: {
            'Content-Type': 'application/json'
        },
    };
	
	let response = http.post(
        `http://app:8080/coupon/issue`,
        payload,
        headers
    );
	//console.log(`Response status: ${response.status}`);
    //console.log(`Response body: ${response.body}`);
    check(response, { 'is status 200': (r) => r.status === 200 });
    return response;
}

function getTopProduct(){
    let headers = {
        headers: {
            'Content-Type': 'application/json'
        },
    };
	
	let response = http.get(
        `http://app:8080/product/top`,
        headers
    );
	//console.log(`Response status: ${response.status}`);
    //console.log(`Response body: ${response.body}`);
    check(response, { 'is status 200': (r) => r.status === 200 });
    return response;
}

function order(userId){
	let procductId1 = randomIntBetween(1, 50);
	let procductId2 = randomIntBetween(1, 50);
	
	let payload = JSON.stringify({
		userId: userId,
		couponId: 0,
        orderDetails:[
	        { productId: procductId1, quantity: 2 },
	        { productId: procductId2, quantity: 1 }
	    ]
    });
    
    let headers = {
        headers: {
            'Content-Type': 'application/json'
        },
    };
	
	let response = http.post(
        `http://app:8080/order`,
        payload,
        headers
    );
	//console.log(`Response status: ${response.status}`);
    //console.log(`Response body: ${response.body}`);
    check(response, { 'is status 200': (r) => r.status === 200 });
    return response;
}

function payment(userId, orderReponse){
	let orderInfo = JSON.parse(orderReponse.body);
	
	let payload = JSON.stringify({
		userId: userId,
        orderId: orderInfo.orderId,
        amount: orderInfo.totalPrice
    });
    
    let headers = {
        headers: {
            'Content-Type': 'application/json'
        },
    };
	
	let response = http.post(
        `http://app:8080/payment`,
        payload,
        headers
    );
	//console.log(`Response status: ${response.status}`);
    //console.log(`Response body: ${response.body}`);
    check(response, { 'is status 200': (r) => r.status === 200 });
    return response;
}
