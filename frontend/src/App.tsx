import { useEffect, useState } from "react";

type Tweet = { id: number; content: string };

export default function App() {
  const [tweets, setTweets] = useState<Tweet[]>([]);
  const [error, setError] = useState("");

  useEffect(() => {
    fetch(`${import.meta.env.VITE_API_URL}/api/tweets/feed`)
      .then((r) => {
        if (!r.ok) throw new Error("Status " + r.status);
        return r.json();
      })
      .then((data) => setTweets(data.content))
      .catch((e) => setError(e.message));
  }, []);

  if (error) return <p>Error: {error}</p>;

  return (
    <ul>
      {tweets.map((t) => (
        <li key={t.id}>{t.content}</li>
      ))}
    </ul>
  );
}