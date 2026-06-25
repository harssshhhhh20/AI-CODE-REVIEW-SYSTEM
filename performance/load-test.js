import http from "k6/http";
import { check } from "k6";

export const options = {
  vus: 300,
  iterations: 300,
};

const payload = JSON.stringify({
  action: "opened",
  pull_request: {
    id: 156789,
    number: 1,
    title: "Your PR Title",
    user: {
      login: "harssshhhhh20",
    },
  },
  repository: {
    name: "RAG-System-for-Codebase",
    owner: {
      login: "harssshhhhh20",
    },
  },
});

const params = {
  headers: {
    "Content-Type": "application/json",
  },
};

export default function () {
  const res = http.post(
    "http://localhost:8080/api/webhooks/github",
    payload,
    params
  );

  console.log(`Status: ${res.status}`);
  console.log(`Body: ${res.body}`);

  check(res, {
    "status is 200": (r) => r.status === 200,
  });
}