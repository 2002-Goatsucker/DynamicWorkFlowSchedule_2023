import numpy as np
import matplotlib.pyplot as plt

if __name__ == "__main__":
    plt.figure(0);
    x = [i + 1 for i in range(500)]
    data1 = np.loadtxt("D:\\000000000WPS\\8创新实践3\\DynamicWorkFlowSchedule_2023\\src\\main\\resources\\result\\result_nsgaiib.txt")
    data2 = np.loadtxt("D:\\000000000WPS\\8创新实践3\\DynamicWorkFlowSchedule_2023\\src\\main\\resources\\result\\result_fogmp.txt")
    data3 = np.loadtxt("D:\\000000000WPS\\8创新实践3\\DynamicWorkFlowSchedule_2023\\src\\main\\resources\\result\\result_idg.txt")
    data4 = np.loadtxt("D:\\000000000WPS\\8创新实践3\\DynamicWorkFlowSchedule_2023\\src\\main\\resources\\result\\result_mb.txt")
    data5 = np.loadtxt("D:\\000000000WPS\\8创新实践3\\DynamicWorkFlowSchedule_2023\\src\\main\\resources\\result\\result_dmga.txt")

    plt.plot(x, data1)
    plt.plot(x, data2)
    plt.plot(x, data3)
    plt.plot(x, data4)
    plt.plot(x, data5)
    plt.legend(['DNSGAIIB', 'FOGMP', 'DNSGAIIgIDG', 'MBNSGAII','DMGA'])
    plt.title("Montage_100 80ins 0.03severity 100task")
    plt.show()