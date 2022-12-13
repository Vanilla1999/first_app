package ru.tander.tsdbrowser.scanners

abstract class AbstractScanner {
    /**
     * onActivityCreate
     * Создает и настрает сканер
     * claim scanner
     */
    abstract fun init()

    /**
     * onActivityResume
     * Подготавливает сканер к работе
     * add listener
     */
    abstract fun prepare()

    /**
     * onActivityPause
     * Приостанавливает сканер
     * remove listener
     */
    abstract fun pause()

    /**
     * onActivityDestroy
     * Освобождает ресурсы сканера, чтобы ссылки не вызывали утечку памяти
     * release scanner
     */
    abstract fun release()

    /**
     * onKeyDown
     * Запускает чтение данных из сканера
     */
    abstract fun startRead()

    /**
     * onKeyUp
     * Прекращает чтение данных из сканера
     */
    abstract fun stopRead()
}