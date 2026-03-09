<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Thank you!!</title>
    <link rel="stylesheet" href="/css/candidates.css">
</head>

<body>
    <div class="container flex flex-col align-center">
        <h1>${candidate.name} thanks you for your vote!</h1>

        <main class="flex flex-col align-center">
            <div class="card">
                <div class="flex flex-col align-center">
                    <img src="/images/${candidate.photo}">
                    <p>Thank you for your vote!</p>
                    <p>Now I have ${percent}% of all votes</p>
                </div>
            </div>

            <p>-------------------------------------------------------</p>
            <a class="back flex align-center" href="/votes">Show total votes</a>

            <p>-------------------------------------------------------</p>
            <a class="back flex align-center" href="/">back to main</a>
        </main>
    </div>
</body>

</html>