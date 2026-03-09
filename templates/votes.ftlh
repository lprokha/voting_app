<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Who's winning?</title>
    <link rel="stylesheet" href="/css/candidates.css">
</head>

<body>
    <div class="container flex flex-col align-center">
        <h1>Who's winning?</h1>
        <main class="flex flex-wrap align-center">
            <#list candidates as candidate>
                <div class="card <#if candidate_index == 0 && totalVotes gt 0>winner</#if>">
                    <div class="flex flex-col align-center">
                        <img src="/images/${candidate.photo}">
                        <p>${candidate.name} has ${percents[candidate.id?c]}% of all votes</p>
                    </div>
                </div>
            </#list>
        </main>

        <p>-------------------------------------------------------</p>
        <a class="back flex align-center" href="/">back to main</a>
    </div>
</body>

</html>