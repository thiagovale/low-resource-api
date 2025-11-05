import http from "k6/http";
import { check } from "k6";

export const options = {
  vus: 500,
  iterations: 1000000,
};

const URL = "http://localhost:8080/process";

const payload = JSON.stringify({
  number: 123,
});

const params = {
  headers: {
    "Content-Type": "application/json",
  },
};

export default function () {
  const res = http.post(URL, payload, params);

  check(res, {
    "is status 202": (r) => r.status === 202,
  });
}
