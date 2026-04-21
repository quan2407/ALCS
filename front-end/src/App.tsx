import MainLayout from './layouts/MainLayout';
import { useEffect } from "react";

function App() {

  useEffect(() => {
    document.title = "Smart Note";
  }, []);

  return (
    <MainLayout>
      <div>Content here</div>
    </MainLayout>
  );
}

export default App;